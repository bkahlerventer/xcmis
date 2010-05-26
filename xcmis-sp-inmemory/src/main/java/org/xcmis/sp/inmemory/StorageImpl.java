/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.sp.inmemory;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.SearchService;
import org.xcmis.search.SearchServiceException;
import org.xcmis.search.Visitors;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.parser.CmisQueryParser;
import org.xcmis.search.parser.QueryParser;
import org.xcmis.search.query.QueryExecutionException;
import org.xcmis.search.result.ScoredRow;
import org.xcmis.search.value.SlashSplitter;
import org.xcmis.search.value.ToStringNameConverter;
import org.xcmis.sp.inmemory.query.CmisContentReader;
import org.xcmis.sp.inmemory.query.CmisSchema;
import org.xcmis.sp.inmemory.query.CmisSchemaTableResolver;
import org.xcmis.sp.inmemory.query.IndexListener;
import org.xcmis.spi.BaseItemsIterator;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PermissionService;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.RenditionManager;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.ACLCapability;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AccessControlPropagation;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CapabilityChanges;
import org.xcmis.spi.model.CapabilityContentStreamUpdatable;
import org.xcmis.spi.model.CapabilityJoin;
import org.xcmis.spi.model.CapabilityQuery;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.ChangeEvent;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.Permission;
import org.xcmis.spi.model.PermissionMapping;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.SupportedPermissions;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.Permission.BasicPermissions;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;
import org.xcmis.spi.utils.CmisDocumentReaderService;
import org.xcmis.spi.utils.CmisUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: StorageImpl.java 804 2010-04-16 16:48:59Z
 *          alexey.zavizionov@gmail.com $
 */
public class StorageImpl implements Storage
{
   private static final Log LOG = ExoLogger.getLogger(StorageImpl.class);

   public static String generateId()
   {
      return UUID.randomUUID().toString();
   }

   final Map<String, Map<String, Value>> properties;

   final Map<String, byte[]> contents;

   final Map<String, Set<String>> children;

   final  Map<String, Set<String>> parents;

   final Set<String> unfiled;

   final Map<String, Set<String>> policies;

   final Map<String, Map<String, Set<String>>> permissions;

   final Map<String, Set<String>> relationships;

   final Map<String, List<String>> versions;

   final Map<String, String> workingCopies;

   final Map<String, TypeDefinition> types;

   final Map<String, Set<String>> typeChildren;

   private RepositoryInfo repositoryInfo;

   /**
    * Searching service.
    */
   final SearchService searchService;

   /**
    * Cmis query parser.
    */
   final QueryParser cmisQueryParser;

   /** The rendition manager. */
   private RenditionManager renditionManager;

   private final StorageConfiguration configuration;

   final IndexListener indexListener;

   static final String ROOT_FOLDER_ID = "abcdef12-3456-7890-0987-654321fedcba";

   static final Set<String> EMPTY_PARENTS = Collections.emptySet();

   private final long maxStorageMemSize;

   private final int maxItemsNumber;

   private PermissionService permissionService;

   public StorageImpl(StorageConfiguration configuration, RenditionManager manager, PermissionService permissionService)
   {
      this(configuration);
      this.renditionManager = manager;
      this.permissionService = permissionService;
   }

   public StorageImpl(StorageConfiguration configuration)
   {
      this.configuration = configuration;

      this.maxStorageMemSize =
         configuration.getProperties().get(StorageConfiguration.MAX_STORAGE_MEM_SIZE) == null
            ? StorageConfiguration.DEFAULT_MAX_STORAGE_MEM_SIZE : StorageConfiguration.parseNumber(
               (String)configuration.getProperties().get(StorageConfiguration.MAX_STORAGE_MEM_SIZE)).longValue();

      this.maxItemsNumber =
         configuration.getProperties().get(StorageConfiguration.MAX_ITEMS_NUMBER) == null
            ? StorageConfiguration.DEFAULT_MAX_STORAGE_NUMBER_ITEMS : StorageConfiguration.parseNumber(
               (String)configuration.getProperties().get(StorageConfiguration.MAX_ITEMS_NUMBER)).intValue();

      this.properties = new ConcurrentHashMap<String, Map<String, Value>>();
      this.children = new ConcurrentHashMap<String, Set<String>>();
      this.parents = new ConcurrentHashMap<String, Set<String>>();

      this.versions = new ConcurrentHashMap<String, List<String>>();
      this.workingCopies = new ConcurrentHashMap<String, String>();

      this.unfiled = new CopyOnWriteArraySet<String>();

      this.contents = new ConcurrentHashMap<String, byte[]>();

      this.relationships = new ConcurrentHashMap<String, Set<String>>();

      this.policies = new ConcurrentHashMap<String, Set<String>>();

      this.permissions = new ConcurrentHashMap<String, Map<String, Set<String>>>();

      this.types = new ConcurrentHashMap<String, TypeDefinition>();

      PermissionMapping permissionMapping = new PermissionMapping();

      permissionMapping.put(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_CHILDREN_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_OBJECT_PARENTS_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_FOLDER_PARENT_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CREATE_FOLDER_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CREATE_RELATIONSHIP_SOURCE, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CREATE_RELATIONSHIP_TARGET, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_PROPERTIES_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_CONTENT_STREAM_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_RENDITIONS_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_MOVE_OBJECT_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_MOVE_OBJECT_TARGET, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_MOVE_OBJECT_SOURCE, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_DELETE_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_DELETE_TREE_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_SET_CONTENT_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_ADD_TO_FOLDER_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_ADD_TO_FOLDER_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_REMOVE_OBJECT_FROM_FOLDER_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_REMOVE_OBJECT_FROM_FOLDER_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CHECKOUT_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_CANCEL_CHECKOUT_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_CHECKIN_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_ALL_VERSIONS_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_OBJECT_RELATIONSHIPS_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_ADD_POLICY_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_ADD_POLICY_POLICY, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_REMOVE_POLICY_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_REMOVE_POLICY_POLICY, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_APPLIED_POLICIES_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_ACL_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_APPLY_ACL_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));

      List<Permission> supportedPermissions = new ArrayList<Permission>(4);
      for (BasicPermissions b : BasicPermissions.values())
      {
         supportedPermissions.add(new Permission(b.value(), ""));
      }

      repositoryInfo =
         new RepositoryInfo(getId(), getId(), ROOT_FOLDER_ID, CmisConstants.SUPPORTED_VERSION,
            new RepositoryCapabilities(CapabilityACL.MANAGE, CapabilityChanges.NONE,
               CapabilityContentStreamUpdatable.ANYTIME, CapabilityJoin.NONE, CapabilityQuery.BOTHCOMBINED,
               CapabilityRendition.READ, false, true, true, true, false, true, true, false), new ACLCapability(
               permissionMapping, Collections.unmodifiableList(supportedPermissions),
               AccessControlPropagation.OBJECTONLY, SupportedPermissions.BASIC), "anonymous", "any", null, null, true,
            null, "eXo", "xCMIS (eXo InMemory SP)", "1.0", null);

      types.put("cmis:document", //
         new TypeDefinition("cmis:document", BaseType.DOCUMENT, "cmis:document", "cmis:document", "", null,
            "cmis:document", "Cmis Document Type", true, true, true, true, true, true, true, true, null, null,
            ContentStreamAllowed.ALLOWED, null));

      types.put("cmis:folder", //
         new TypeDefinition("cmis:folder", BaseType.FOLDER, "cmis:folder", "cmis:folder", "", null, "cmis:folder",
            "Cmis Folder type", true, true, true, false, true, true, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, null));

      types.put("cmis:policy", //
         new TypeDefinition("cmis:policy", BaseType.POLICY, "cmis:policy", "cmis:policy", "", null, "cmis:policy",
            "Cmis Policy type", true, false, false /*no query support*/, false, false, true, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, null));

      types.put("cmis:relationship", //
         new TypeDefinition("cmis:relationship", BaseType.RELATIONSHIP, "cmis:relationship", "cmis:relationship", "",
            null, "cmis:relationship", "Cmis Relationship type.", true, false, false /*no query support*/, false,
            false, true, true, false, null, null, ContentStreamAllowed.NOT_ALLOWED, null));

      typeChildren = new ConcurrentHashMap<String, Set<String>>();
      typeChildren.put("cmis:document", new HashSet<String>());
      typeChildren.put("cmis:folder", new HashSet<String>());
      typeChildren.put("cmis:policy", new HashSet<String>());
      typeChildren.put("cmis:relationship", new HashSet<String>());

      Map<String, Value> root = new ConcurrentHashMap<String, Value>();
      root.put(CmisConstants.NAME, //
         new StringValue(""));
      root.put(CmisConstants.OBJECT_ID, //
         new StringValue(ROOT_FOLDER_ID));
      root.put(CmisConstants.OBJECT_TYPE_ID, //
         new StringValue("cmis:folder"));
      root.put(CmisConstants.BASE_TYPE_ID, //
         new StringValue(BaseType.FOLDER.value()));
      root.put(CmisConstants.CREATION_DATE, //
         new DateValue(Calendar.getInstance()));
      root.put(CmisConstants.CREATED_BY, //
         new StringValue("system"));
      root.put(CmisConstants.LAST_MODIFICATION_DATE, //
         new DateValue(Calendar.getInstance()));
      root.put(CmisConstants.LAST_MODIFIED_BY, //
         new StringValue("system"));

      properties.put(ROOT_FOLDER_ID, root);
      policies.put(ROOT_FOLDER_ID, new HashSet<String>());
      permissions.put(ROOT_FOLDER_ID, new HashMap<String, Set<String>>());
      parents.put(ROOT_FOLDER_ID, EMPTY_PARENTS);
      children.put(ROOT_FOLDER_ID, new CopyOnWriteArraySet<String>());

      this.searchService = getInitializedSearchService();
      this.indexListener = new IndexListener(this, searchService);
      this.cmisQueryParser = new CmisQueryParser();
   }

   /**
    * {@inheritDoc}
    */
   public AllowableActions calculateAllowableActions(ObjectData object)
   {
      AllowableActions actions =
         permissionService.calculateAllowableActions(object, ConversationState.getCurrent().getIdentity(),
            getRepositoryInfo());

      return actions;
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData copyDocument(DocumentData source, FolderData parent, Map<String, Property<?>> properties,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<ObjectData> policies,
      VersioningState versioningState) throws ConstraintException, StorageException
   {
      if (parent != null)
      {
         if (!parent.isAllowedChildType(source.getTypeId()))
         {
            throw new ConstraintException("Type " + source.getTypeId()
               + " is not in list of allowed child type for folder " + parent.getObjectId());
         }
      }

      if (source.getBaseType() != BaseType.DOCUMENT)
      {
         throw new ConstraintException("Source object has type whose base type is not Document.");
      }

      //return new DocumentCopy(source, parent, getTypeDefinition(source.getTypeId(), true), versioningState, this);
      DocumentCopy obj = new DocumentCopy(source, parent, getTypeDefinition(source.getTypeId(), true), versioningState, this);
      if (properties != null)
      {
         for (Property<?> property : properties.values())
         {
            ((BaseObjectData)obj).setPropertyInternal(property);
         }
      }
      
      if (policies != null)
      {
         for (ObjectData policy : policies)
         {
            ((BaseObjectData)obj).applyPolicyInternal((PolicyData)policy);
         }
      }
      
      List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(obj.getACL(false), addACL, removeACL);
      obj.setACLInternal(mergedACL);
      
      obj.save(true);
      return obj;
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData createDocument(FolderData parent, String typeId, Map<String, Property<?>> properties,
      ContentStream content, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      Collection<ObjectData> policies, VersioningState versioningState) throws ConstraintException
   {
      if (parent != null)
      {
         if (!parent.isAllowedChildType(typeId))
         {
            throw new ConstraintException("Type " + typeId + " is not in list of allowed child type for folder "
               + parent.getObjectId());
         }
      }

      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.DOCUMENT)
      {
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Document.");
      }

      //return new DocumentDataImpl(parent, typeDefinition, versioningState, this);
      DocumentDataImpl obj = new DocumentDataImpl(parent, typeDefinition, versioningState, this);
      if (content != null)
      {
         obj.setContentStreamInternal(content);
      }
      
      if (properties != null)
      {
         for (Property<?> property : properties.values())
         {
            ((BaseObjectData)obj).setPropertyInternal(property);
         }
      }
      
      if (policies != null)
      {
         for (ObjectData policy : policies)
         {
            ((BaseObjectData)obj).applyPolicyInternal((PolicyData)policy);
         }
      }
      
      List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(obj.getACL(false), addACL, removeACL);
      obj.setACLInternal(mergedACL);
      
      
      obj.save(true);
      return obj;
   }

   
    
   /**
    * {@inheritDoc}
    */
   public FolderData createFolder(FolderData parent, String typeId, Map<String, Property<?>> properties,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<ObjectData> policies)
      throws ConstraintException
   {
      if (parent == null)
      {
         throw new ConstraintException("Parent folder must be provided.");
      }

      if (!parent.isAllowedChildType(typeId))
      {
         throw new ConstraintException("Type " + typeId + " is not in list of allowed child type for folder "
            + parent.getObjectId());
      }

      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.FOLDER)
      {
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Folder.");
      }

      //return new FolderDataImpl(parent, typeDefinition, this);
      FolderDataImpl obj = new FolderDataImpl(parent, typeDefinition, this);
      
      if (properties != null)
      {
         for (Property<?> property : properties.values())
         {
            ((BaseObjectData)obj).setPropertyInternal(property);
         }
      }
      
      if (policies != null)
      {
         for (ObjectData policy : policies)
         {
            ((BaseObjectData)obj).applyPolicyInternal((PolicyData)policy);
         }
      }
      
      List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(obj.getACL(false), addACL, removeACL);
      obj.setACLInternal(mergedACL);
      
      obj.save(true);
      return obj;
   }

   /**
    * {@inheritDoc}
    */
   public PolicyData createPolicy(FolderData parent, String typeId, Map<String, Property<?>> properties,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<ObjectData> policies) throws ConstraintException
   {
      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.POLICY)
      {
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Policy.");
      }

      //return new PolicyDataImpl(typeDefinition, this);
      PolicyDataImpl obj = new PolicyDataImpl(typeDefinition, this);
      
      if (properties != null)
      {
         for (Property<?> property : properties.values())
         {
            ((BaseObjectData)obj).setPropertyInternal(property);
         }
      }
      
      if (policies != null)
      {
         for (ObjectData policy : policies)
         {
            ((BaseObjectData)obj).applyPolicyInternal((PolicyData)policy);
         }
      }
      
      obj.save(true);
      
      List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(obj.getACL(false), addACL, removeACL);
      obj.setACLInternal(mergedACL);
      
      return obj;
   }

   /**
    * {@inheritDoc}
    */
   public RelationshipData createRelationship(ObjectData source, ObjectData target, String typeId, Map<String, Property<?>> properties,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<ObjectData> policies)
      throws ConstraintException
   {
      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.RELATIONSHIP)
      {
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Relationship.");
      }

      //return new RelationshipDataImpl(getTypeDefinition(typeId, true), source, target, this);
      RelationshipDataImpl obj = new RelationshipDataImpl(getTypeDefinition(typeId, true), source, target, this);

      if (properties != null)
      {
         for (Property<?> property : properties.values())
         {
            ((BaseObjectData)obj).setPropertyInternal(property);
         }
      }
      
      if (policies != null)
      {
         for (ObjectData policy : policies)
         {
            ((BaseObjectData)obj).applyPolicyInternal((PolicyData)policy);
         }
      }
      
      List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(obj.getACL(false), addACL, removeACL);
      obj.setACLInternal(mergedACL);
      
      obj.save(true);
      return obj;
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(ObjectData object, boolean deleteAllVersions) throws ConstraintException,
      UpdateConflictException, StorageException
   {
      String objectId = object.getObjectId();

      ((BaseObjectData)object).delete();

      if (indexListener != null)
      {
         Set<String> removed = new HashSet<String>();
         removed.add(objectId);
         indexListener.removed(removed);
      }
   }

   public Collection<String> deleteTree(FolderData folder, boolean deleteAllVersions, UnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException
   {
      // TODO : unfile & continueOnFailure

      if (ROOT_FOLDER_ID.equals(folder.getObjectId()))
      {
         throw new ConstraintException("Unable delete root folder.");
      }

      final Collection<String> failedToDelete = new ArrayList<String>();

      for (ItemsIterator<ObjectData> iterator = folder.getChildren(null); iterator.hasNext();)
      {
         ObjectData object = iterator.next();
         if (object.getBaseType() == BaseType.FOLDER)
         {
            deleteTree((FolderData)object, deleteAllVersions, unfileObject, continueOnFailure);
         }
         else
         {
            try
            {
               deleteObject(object, false);
            }
            catch (StorageException e)
            {
               failedToDelete.add(object.getObjectId());
            }
         }
      }

      try
      {
         deleteObject(folder, false);
      }
      catch (StorageException e)
      {
         failedToDelete.add(folder.getObjectId());
      }

      return failedToDelete;
   }

   public Collection<DocumentData> getAllVersions(String versionSeriesId) throws ObjectNotFoundException
   {
      List<DocumentData> v = new ArrayList<DocumentData>();
      if (!workingCopies.containsKey(versionSeriesId) && !versions.containsKey(versionSeriesId))
      {
         throw new ObjectNotFoundException("Version series '" + versionSeriesId + "' does not exist.");
      }
      String pwc = workingCopies.get(versionSeriesId);
      if (pwc != null)
      {
         v.add((DocumentData)getObjectById(pwc));
      }
      for (String vId : versions.get(versionSeriesId))
      {
         v.add((DocumentData)getObjectById(vId));
      }
      return v;
   }

   public ItemsIterator<ChangeEvent> getChangeLog(String changeLogToken) throws ConstraintException
   {
      // TODO
      return CmisUtils.emptyItemsIterator();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<DocumentData> getCheckedOutDocuments(ObjectData folder, String orderBy)
   {
      List<DocumentData> checkedOut = new ArrayList<DocumentData>();

      for (String pwcId : workingCopies.values())
      {
         DocumentData pwc = (DocumentData)getObjectById(pwcId);
         if (folder != null)
         {
            for (FolderData parent : pwc.getParents())
            {
               // TODO equals and hashCode for objects
               if (parent.getObjectId().equals(folder.getObjectId()))
               {
                  checkedOut.add(pwc);
               }
            }
         }
         else
         {
            checkedOut.add(pwc);
         }
      }
      return new BaseItemsIterator<DocumentData>(checkedOut);
   }

   public String getId()
   {
      return configuration.getId();
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData getObjectById(String objectId) throws ObjectNotFoundException
   {
      Map<String, Value> values = properties.get(objectId);
      if (values == null)
      {
         throw new ObjectNotFoundException("Object '" + objectId + "' does not exist.");
      }
      BaseType baseType = BaseType.fromValue(values.get(CmisConstants.BASE_TYPE_ID).getStrings()[0]);
      String typeId = values.get(CmisConstants.OBJECT_TYPE_ID).getStrings()[0];
      switch (baseType)
      {
         case DOCUMENT :
            return new DocumentDataImpl(new Entry(new HashMap<String, Value>(values), new HashMap<String, Set<String>>(
               permissions.get(objectId)), new HashSet<String>(policies.get(objectId))),
               getTypeDefinition(typeId, true), this, renditionManager);
         case FOLDER :
            return new FolderDataImpl(new Entry(new HashMap<String, Value>(values), new HashMap<String, Set<String>>(
               permissions.get(objectId)), new HashSet<String>(policies.get(objectId))),
               getTypeDefinition(typeId, true), this);
         case POLICY :
            return new PolicyDataImpl(new Entry(new HashMap<String, Value>(values), new HashMap<String, Set<String>>(
               permissions.get(objectId)), new HashSet<String>(policies.get(objectId))),
               getTypeDefinition(typeId, true), this);
         case RELATIONSHIP :
            return new RelationshipDataImpl(
               new Entry(new HashMap<String, Value>(values),
                  new HashMap<String, Set<String>>(permissions.get(objectId)), new HashSet<String>(policies
                     .get(objectId))), getTypeDefinition(typeId, true), this);
      }
      // Must never happen.
      throw new CmisRuntimeException("Unknown base type. ");
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData getObjectByPath(String path) throws ObjectNotFoundException
   {
      if (!path.startsWith("/"))
      {
         path = "/" + path;
      }
      StringTokenizer tokenizer = new StringTokenizer(path, "/");
      String point = StorageImpl.ROOT_FOLDER_ID;
      while (tokenizer.hasMoreTokens())
      {
         if (point == null)
         {
            break;
         }
         String segName = tokenizer.nextToken();
         Set<String> childrenIds = children.get(point);
         if (childrenIds == null || childrenIds.isEmpty())
         {
            point = null;
         }
         else
         {
            for (String id : childrenIds)
            {
               ObjectData seg = getObjectById(id);
               String name = seg.getName();
               if ((BaseType.FOLDER == seg.getBaseType() || !tokenizer.hasMoreElements()) && segName.equals(name))
               {
                  point = id;
                  break;
               }
               point = null;
            }
         }
      }

      if (point == null)
      {
         throw new ObjectNotFoundException("Path '" + path + "' not found.");
      }
      return getObjectById(point);
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Rendition> getRenditions(ObjectData object)
   {
      if (renditionManager != null)
      {
         ArrayList<Rendition> renditionList = new ArrayList<Rendition>();
         Rendition rend = renditionManager.getRenditions(object);
         if (rend != null)
            renditionList.add(rend);
         return new BaseItemsIterator<Rendition>(renditionList);
      }
      return CmisUtils.emptyItemsIterator();
   }

   /**
    * {@inheritDoc}
    */
   public RepositoryInfo getRepositoryInfo()
   {
      return repositoryInfo;
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData moveObject(ObjectData object, FolderData target, FolderData source) throws ConstraintException,
      InvalidArgumentException, UpdateConflictException, VersioningException, NameConstraintViolationException,
      StorageException
   {
      String objectid = object.getObjectId();
      String sourceId = source.getObjectId();
      String targetId = target.getObjectId();
      children.get(sourceId).remove(objectid);
      children.get(targetId).add(objectid);
      parents.get(object.getObjectId()).remove(sourceId);
      parents.get(object.getObjectId()).add(targetId);
      return getObjectById(objectid);
   }

   public ItemsIterator<Result> query(Query query) throws InvalidArgumentException
   {
      try
      {
         org.xcmis.search.model.Query qom = cmisQueryParser.parseQuery(query.getStatement());
         List<ScoredRow> rows = searchService.execute(qom);
         //check if needed default sorting
         if (qom.getOrderings().size() == 0)
         {

            Set<SelectorName> selectorsReferencedBy = Visitors.getSelectorsReferencedBy(qom);
            Collections.sort(rows, new DocumentOrderResultSorter(selectorsReferencedBy.iterator().next().getName(),
               this));
         }
         return new QueryResultIterator(rows, qom);
      }
      catch (InvalidQueryException e)
      {
         throw new InvalidArgumentException(e.getLocalizedMessage(), e);
      }
      catch (QueryExecutionException e)
      {
         throw new CmisRuntimeException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void unfileObject(ObjectData object)
   {
      Set<String> parentIds = parents.get(object.getObjectId());
      for (String id : parentIds)
      {
         children.get(id).remove(object.getObjectId());
      }
      parents.clear();
   }

   /**
    * @see org.xcmis.spi.Storage#getUnfiledObjects()
    */
   public Iterator<String> getUnfiledObjectsId() throws StorageException
   {
      return unfiled.iterator();
   }

   public String addType(TypeDefinition type) throws StorageException, CmisRuntimeException
   {
      if (types.get(type.getId()) != null)
      {
         throw new InvalidArgumentException("Type " + type.getId() + " already exists.");
      }
      if (type.getBaseId() == null)
      {
         throw new InvalidArgumentException("Base type id must be specified.");
      }
      if (type.getParentId() == null)
      {
         throw new InvalidArgumentException("Unable add root type. Parent type id must be specified");
      }

      TypeDefinition superType;
      try
      {
         superType = getTypeDefinition(type.getParentId(), true);
      }
      catch (TypeNotFoundException e)
      {
         throw new InvalidArgumentException("Specified parent type '" + type.getParentId() + "' does not exist.");
      }
      // Check new type does not use known property IDs.
      if (type.getPropertyDefinitions() != null)
      {
         for (PropertyDefinition<?> newDefinition : type.getPropertyDefinitions())
         {
            PropertyDefinition<?> definition = superType.getPropertyDefinition(newDefinition.getId());
            if (definition != null)
            {
               throw new InvalidArgumentException("Property " + newDefinition.getId() + " already defined");
            }
         }
      }

      Map<String, PropertyDefinition<?>> m = new HashMap<String, PropertyDefinition<?>>();
      for (Iterator<PropertyDefinition<?>> iterator = superType.getPropertyDefinitions().iterator(); iterator.hasNext();)
      {
         PropertyDefinition<?> next = iterator.next();
         m.put(next.getId(), next);
      }

      if (type.getPropertyDefinitions() != null)
      {
         for (Iterator<PropertyDefinition<?>> iterator = type.getPropertyDefinitions().iterator(); iterator.hasNext();)
         {
            PropertyDefinition<?> next = iterator.next();
            m.put(next.getId(), next);
         }
      }

      types.put(type.getId(), type);
      typeChildren.get(superType.getId()).add(type.getId());
      typeChildren.put(type.getId(), new HashSet<String>());
      PropertyDefinitions.putAll(type.getId(), m);

      return type.getId();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinitions)
      throws TypeNotFoundException, CmisRuntimeException
   {
      List<TypeDefinition> types = new ArrayList<TypeDefinition>();
      if (typeId == null)
      {
         for (String t : new String[]{"cmis:document", "cmis:folder", "cmis:policy", "cmis:relationship"})
         {
            types.add(getTypeDefinition(t, includePropertyDefinitions));
         }
      }
      else
      {
         if (this.types.get(typeId) == null)
         {
            throw new TypeNotFoundException("Type '" + typeId + "' does not exist.");
         }

         for (String t : typeChildren.get(typeId))
         {
            types.add(getTypeDefinition(t, includePropertyDefinitions));
         }
      }
      return new BaseItemsIterator<TypeDefinition>(types);
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      TypeDefinition type = types.get(typeId);
      if (type == null)
      {
         throw new TypeNotFoundException("Type '" + typeId + "' does not exist.");
      }
      TypeDefinition copy =
         new TypeDefinition(type.getId(), type.getBaseId(), type.getQueryName(), type.getLocalName(), type
            .getLocalNamespace(), type.getParentId(), type.getDisplayName(), type.getDescription(), type.isCreatable(),
            type.isFileable(), type.isQueryable(), type.isFulltextIndexed(), type.isIncludedInSupertypeQuery(), type
               .isControllablePolicy(), type.isControllableACL(), type.isVersionable(), type.getAllowedSourceTypes(),
            type.getAllowedTargetTypes(), type.getContentStreamAllowed(), includePropertyDefinition
               ? PropertyDefinitions.getAll(typeId) : null);

      return copy;
   }

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, StorageException, CmisRuntimeException
   {
      TypeDefinition type = types.get(typeId);
      if (type == null)
      {
         throw new TypeNotFoundException("Type '" + typeId + "' does not exist.");
      }

      if (type.getParentId() == null)
      {
         throw new ConstraintException("Unable remove root type " + typeId);
      }

      if (typeChildren.get(typeId).size() > 0)
      {
         throw new ConstraintException("Unable remove type " + typeId + ". Type has descendant types.");
      }

      for (Iterator<Map<String, Value>> iterator = properties.values().iterator(); iterator.hasNext();)
      {
         if (typeId.equals(iterator.next().get(CmisConstants.OBJECT_TYPE_ID).getStrings()[0]))
         {
            throw new ConstraintException("Unable remove type definition if at least one object of this type exists.");
         }
      }
      types.remove(typeId);
      typeChildren.get(type.getParentId()).remove(typeId);

      PropertyDefinitions.removeAll(typeId);
   }

   void validateMaxItemsNumber(ObjectData object)
   {
      if (properties.size() > maxItemsNumber)
      {
         throw new CmisRuntimeException("Unable add new object in storage. Max number '" + maxItemsNumber
            + "' of items is reached."
            + " Increase or set storage configuration property 'org.xcmis.inmemory.maxitems'.");
      }
   }

   void validateMemSize(byte[] content)
   {
      if (content == null || content.length == 0)
      {
         return;
      }
      long size = 0;
      for (byte[] c : contents.values())
      {
         size += c.length;
      }
      if ((size + content.length) > maxStorageMemSize)
      {
         throw new CmisRuntimeException("Unable add new object in storage. Max allowed memory size '"
            + maxStorageMemSize + "' bytes is reached."
            + " Increase or set storage configuration property 'org.xcmis.inmemory.maxmem'.");
      }
   }

   private SearchService getInitializedSearchService()
   {
      CmisSchema schema = new CmisSchema(this);
      CmisSchemaTableResolver tableResolver = new CmisSchemaTableResolver(new ToStringNameConverter(), schema, this);

      IndexConfiguration indexConfiguration = new IndexConfiguration();
      indexConfiguration.setQueryableIndexStorage("org.xcmis.search.lucene.InMemoryLuceneQueryableIndexStorage");
      //indexConfiguration.setIndexDir("/tmp/dir/" + UUID.randomUUID().toString() + "/");
      indexConfiguration.setRootUuid(this.getRepositoryInfo().getRootFolderId());
      //if list of root parents is empty it will be indexed as empty string
      indexConfiguration.setRootParentUuid("");
      indexConfiguration.setDocumentReaderService(new CmisDocumentReaderService());

      //default invocation context
      InvocationContext invocationContext = new InvocationContext();
      invocationContext.setNameConverter(new ToStringNameConverter());

      invocationContext.setSchema(schema);
      invocationContext.setPathSplitter(new SlashSplitter());

      invocationContext.setTableResolver(tableResolver);

      SearchServiceConfiguration searchConfiguration = new SearchServiceConfiguration();
      searchConfiguration.setIndexConfiguration(indexConfiguration);
      searchConfiguration.setContentReader(new CmisContentReader(this));
      searchConfiguration.setNameConverter(new ToStringNameConverter());
      searchConfiguration.setDefaultInvocationContext(invocationContext);
      searchConfiguration.setTableResolver(tableResolver);
      searchConfiguration.setPathSplitter(new SlashSplitter());

      try
      {
         SearchService searchService = new SearchService(searchConfiguration);
         searchService.start();
         return searchService;
         //attach listener to the created storage
         //IndexListener indexListener = new IndexListener(this, searchService);
         //storage.setIndexListener(indexListener);

      }
      catch (SearchServiceException e)
      {
         LOG.error("Unable to initialize storage. ", e);
      }
      return null;

   }

   public static class DocumentOrderResultSorter implements Comparator<ScoredRow>
   {

      /** The selector name. */
      private final String selectorName;

      private final Map<String, ObjectData> itemCache;

      private final Storage storage;

      /**
       * The Constructor.
       *
       * @param itemMgr the item mgr
       * @param selectorName the selector name
       */
      public DocumentOrderResultSorter(final String selectorName, Storage storage)
      {
         this.selectorName = selectorName;
         this.storage = storage;
         this.itemCache = new HashMap<String, ObjectData>();
      }

      /**
       * {@inheritDoc}
       */
      public int compare(ScoredRow o1, ScoredRow o2)
      {
         if (o1.equals(o2))
         {
            return 0;
         }
         final String path1 = getPath(o1.getNodeIdentifer(selectorName));
         final String path2 = getPath(o2.getNodeIdentifer(selectorName));
         // TODO should be checked
         if (path1 == null || path2 == null)
         {
            return 0;
         }
         return path1.compareTo(path2);
      }

      /**
       * Return comparable location of the object
       *
       * @param identifer
       * @return
       */
      public String getPath(String identifer)
      {
         ObjectData obj = itemCache.get(identifer);
         if (obj == null)
         {
            obj = storage.getObjectById(identifer);
            itemCache.put(identifer, obj);
         }
         if (obj.getBaseType() == BaseType.FOLDER)
         {
            if (((FolderData)obj).isRoot())
            {
               return obj.getName();
            }
         }
         FolderData parent = obj.getParent();
         if (parent == null)
         {
            return obj.getName();
         }
         return parent.getPath() + "/" + obj.getName();
      }
   }

   /**
    * Single row from query result.
    */
   public static class ResultImpl implements Result
   {

      private final String id;

      private final String[] properties;

      private final Score score;

      public ResultImpl(String id, String[] properties, Score score)
      {
         this.id = id;
         this.properties = properties;
         this.score = score;
      }

      public String[] getPropertyNames()
      {
         return properties;
      }

      public String getObjectId()
      {
         return id;
      }

      public Score getScore()
      {
         return score;
      }

   }

   /**
    * Iterator over query result's.
    */
   private static class QueryResultIterator implements ItemsIterator<Result>
   {

      private final Iterator<ScoredRow> rows;

      private final Set<SelectorName> selectors;

      private final int size;

      private final org.xcmis.search.model.Query qom;

      private Result next;

      public QueryResultIterator(List<ScoredRow> rows, org.xcmis.search.model.Query qom)
      {
         this.size = rows.size();
         this.rows = rows.iterator();
         this.selectors = Visitors.getSelectorsReferencedBy(qom);
         this.qom = qom;
         fetchNext();
      }

      /**
       * {@inheritDoc}
       */
      public boolean hasNext()
      {
         return next != null;
      }

      /**
       * {@inheritDoc}
       */
      public Result next()
      {
         if (next == null)
         {
            throw new NoSuchElementException();
         }
         Result r = next;
         fetchNext();
         return r;
      }

      /**
       * {@inheritDoc}
       */
      public void remove()
      {
         throw new UnsupportedOperationException("remove");
      }

      /**
       * {@inheritDoc}
       */
      public int size()
      {
         return size;
      }

      /**
       * {@inheritDoc}
       */
      public void skip(int skip) throws NoSuchElementException
      {
         while (skip-- > 0)
         {
            next();
         }
      }

      /**
       * To fetch next <code>Result</code>.
       */
      protected void fetchNext()
      {
         next = null;
         while (next == null && rows.hasNext())
         {
            ScoredRow row = rows.next();
            for (SelectorName selectorName : selectors)
            {
               String objectId = row.getNodeIdentifer(selectorName.getName());
               List<String> properties = null;
               Score score = null;
               for (Column column : qom.getColumns())
               {
                  //TODO check
                  if (column.isFunction())
                  {
                     score = new Score(column.getColumnName(), BigDecimal.valueOf(row.getScore()));
                  }
                  else
                  {
                     if (selectorName.getName().equals(column.getSelectorName()))
                     {
                        if (column.getPropertyName() != null)
                        {
                           if (properties == null)
                           {
                              properties = new ArrayList<String>();
                           }
                           properties.add(column.getPropertyName());
                        }
                     }
                  }
               }
               next = new ResultImpl(objectId, //
                  properties == null ? null : properties.toArray(new String[properties.size()]), //
                  score);
            }
         }
      }
   }

   public static class LazyDocumentIterator implements ItemsIterator<ObjectData>
   {
      private final Storage storage;

      private final Iterator<String> documentIds;

      /**
       * @param storage
       * @param documentIds
       */
      public LazyDocumentIterator(Storage storage, Iterator<String> documentIds)
      {
         super();
         this.storage = storage;
         this.documentIds = documentIds;
      }

      /**
       * @see java.util.Iterator#hasNext()
       */
      public boolean hasNext()
      {
         return documentIds.hasNext();
      }

      /**
       * @see java.util.Iterator#next()
       */
      public DocumentData next()
      {
         return (DocumentData)storage.getObjectById(documentIds.next());
      }

      /**
       * @see java.util.Iterator#remove()
       */
      public void remove()
      {
         throw new UnsupportedOperationException();
      }

      /**
       * @see org.xcmis.spi.ItemsIterator#size()
       */
      public int size()
      {

         return -1;
      }

      /**
       * @see org.xcmis.spi.ItemsIterator#skip(int)
       */
      public void skip(int skip) throws NoSuchElementException
      {
         while (skip-- > 0)
         {
            if (!documentIds.hasNext())
            {
               throw new NoSuchElementException();
            }
            documentIds.next();
         }

      }

   }
}

// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: FilePathResult.proto

package com.github.sioncheng.prtl.outer;

public final class FindFileResult {
  private FindFileResult() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface FindFileResultMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:FindFileResultMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required string path = 1;</code>
     */
    boolean hasPath();
    /**
     * <code>required string path = 1;</code>
     */
    String getPath();
    /**
     * <code>required string path = 1;</code>
     */
    com.google.protobuf.ByteString
        getPathBytes();

    /**
     * <code>required bool success = 2;</code>
     */
    boolean hasSuccess();
    /**
     * <code>required bool success = 2;</code>
     */
    boolean getSuccess();
  }
  /**
   * Protobuf type {@code FindFileResultMessage}
   */
  public  static final class FindFileResultMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:FindFileResultMessage)
      FindFileResultMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use FindFileResultMessage.newBuilder() to construct.
    private FindFileResultMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private FindFileResultMessage() {
      path_ = "";
      success_ = false;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private FindFileResultMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              path_ = bs;
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              success_ = input.readBool();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return FindFileResult.internal_static_FindFileResultMessage_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return FindFileResult.internal_static_FindFileResultMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              FindFileResultMessage.class, Builder.class);
    }

    private int bitField0_;
    public static final int PATH_FIELD_NUMBER = 1;
    private volatile Object path_;
    /**
     * <code>required string path = 1;</code>
     */
    public boolean hasPath() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string path = 1;</code>
     */
    public String getPath() {
      Object ref = path_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          path_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string path = 1;</code>
     */
    public com.google.protobuf.ByteString
        getPathBytes() {
      Object ref = path_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        path_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int SUCCESS_FIELD_NUMBER = 2;
    private boolean success_;
    /**
     * <code>required bool success = 2;</code>
     */
    public boolean hasSuccess() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required bool success = 2;</code>
     */
    public boolean getSuccess() {
      return success_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasPath()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasSuccess()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, path_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBool(2, success_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, path_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(2, success_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof FindFileResultMessage)) {
        return super.equals(obj);
      }
      FindFileResultMessage other = (FindFileResultMessage) obj;

      boolean result = true;
      result = result && (hasPath() == other.hasPath());
      if (hasPath()) {
        result = result && getPath()
            .equals(other.getPath());
      }
      result = result && (hasSuccess() == other.hasSuccess());
      if (hasSuccess()) {
        result = result && (getSuccess()
            == other.getSuccess());
      }
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasPath()) {
        hash = (37 * hash) + PATH_FIELD_NUMBER;
        hash = (53 * hash) + getPath().hashCode();
      }
      if (hasSuccess()) {
        hash = (37 * hash) + SUCCESS_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
            getSuccess());
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static FindFileResultMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static FindFileResultMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static FindFileResultMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static FindFileResultMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static FindFileResultMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static FindFileResultMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static FindFileResultMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static FindFileResultMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static FindFileResultMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static FindFileResultMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static FindFileResultMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static FindFileResultMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(FindFileResultMessage prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code FindFileResultMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:FindFileResultMessage)
        FindFileResultMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return FindFileResult.internal_static_FindFileResultMessage_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return FindFileResult.internal_static_FindFileResultMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                FindFileResultMessage.class, Builder.class);
      }

      // Construct using com.github.sioncheng.prtl.outer.FindFileResult.FindFileResultMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        path_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        success_ = false;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return FindFileResult.internal_static_FindFileResultMessage_descriptor;
      }

      public FindFileResultMessage getDefaultInstanceForType() {
        return FindFileResultMessage.getDefaultInstance();
      }

      public FindFileResultMessage build() {
        FindFileResultMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public FindFileResultMessage buildPartial() {
        FindFileResultMessage result = new FindFileResultMessage(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.path_ = path_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.success_ = success_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof FindFileResultMessage) {
          return mergeFrom((FindFileResultMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(FindFileResultMessage other) {
        if (other == FindFileResultMessage.getDefaultInstance()) return this;
        if (other.hasPath()) {
          bitField0_ |= 0x00000001;
          path_ = other.path_;
          onChanged();
        }
        if (other.hasSuccess()) {
          setSuccess(other.getSuccess());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (!hasPath()) {
          return false;
        }
        if (!hasSuccess()) {
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        FindFileResultMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (FindFileResultMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private Object path_ = "";
      /**
       * <code>required string path = 1;</code>
       */
      public boolean hasPath() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string path = 1;</code>
       */
      public String getPath() {
        Object ref = path_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            path_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string path = 1;</code>
       */
      public com.google.protobuf.ByteString
          getPathBytes() {
        Object ref = path_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b =
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          path_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string path = 1;</code>
       */
      public Builder setPath(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        path_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string path = 1;</code>
       */
      public Builder clearPath() {
        bitField0_ = (bitField0_ & ~0x00000001);
        path_ = getDefaultInstance().getPath();
        onChanged();
        return this;
      }
      /**
       * <code>required string path = 1;</code>
       */
      public Builder setPathBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        path_ = value;
        onChanged();
        return this;
      }

      private boolean success_ ;
      /**
       * <code>required bool success = 2;</code>
       */
      public boolean hasSuccess() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required bool success = 2;</code>
       */
      public boolean getSuccess() {
        return success_;
      }
      /**
       * <code>required bool success = 2;</code>
       */
      public Builder setSuccess(boolean value) {
        bitField0_ |= 0x00000002;
        success_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bool success = 2;</code>
       */
      public Builder clearSuccess() {
        bitField0_ = (bitField0_ & ~0x00000002);
        success_ = false;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:FindFileResultMessage)
    }

    // @@protoc_insertion_point(class_scope:FindFileResultMessage)
    private static final FindFileResultMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new FindFileResultMessage();
    }

    public static FindFileResultMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @Deprecated public static final com.google.protobuf.Parser<FindFileResultMessage>
        PARSER = new com.google.protobuf.AbstractParser<FindFileResultMessage>() {
      public FindFileResultMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new FindFileResultMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<FindFileResultMessage> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<FindFileResultMessage> getParserForType() {
      return PARSER;
    }

    public FindFileResultMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_FindFileResultMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_FindFileResultMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\024FilePathResult.proto\"6\n\025FindFileResult" +
      "Message\022\014\n\004path\030\001 \002(\t\022\017\n\007success\030\002 \002(\010B1" +
      "\n\037com.github.sioncheng.prtl.outerB\016FindF" +
      "ileResult"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_FindFileResultMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_FindFileResultMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_FindFileResultMessage_descriptor,
        new String[] { "Path", "Success", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
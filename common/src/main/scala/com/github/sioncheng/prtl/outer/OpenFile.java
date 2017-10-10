// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OpenFile.proto

package com.github.sioncheng.prtl.outer;

public final class OpenFile {
  private OpenFile() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface OpenFileMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:OpenFileMessage)
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
     * <code>required int64 contentLength = 2;</code>
     */
    boolean hasContentLength();
    /**
     * <code>required int64 contentLength = 2;</code>
     */
    long getContentLength();

    /**
     * <code>required int32 partitions = 3;</code>
     */
    boolean hasPartitions();
    /**
     * <code>required int32 partitions = 3;</code>
     */
    int getPartitions();

    /**
     * <code>required int32 partitionNo = 4;</code>
     */
    boolean hasPartitionNo();
    /**
     * <code>required int32 partitionNo = 4;</code>
     */
    int getPartitionNo();

    /**
     * <code>required int32 partitionLength = 5;</code>
     */
    boolean hasPartitionLength();
    /**
     * <code>required int32 partitionLength = 5;</code>
     */
    int getPartitionLength();

    /**
     * <code>required bytes data = 6;</code>
     */
    boolean hasData();
    /**
     * <code>required bytes data = 6;</code>
     */
    com.google.protobuf.ByteString getData();
  }
  /**
   * Protobuf type {@code OpenFileMessage}
   */
  public  static final class OpenFileMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:OpenFileMessage)
      OpenFileMessageOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use OpenFileMessage.newBuilder() to construct.
    private OpenFileMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private OpenFileMessage() {
      path_ = "";
      contentLength_ = 0L;
      partitions_ = 0;
      partitionNo_ = 0;
      partitionLength_ = 0;
      data_ = com.google.protobuf.ByteString.EMPTY;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private OpenFileMessage(
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
              contentLength_ = input.readInt64();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              partitions_ = input.readInt32();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              partitionNo_ = input.readInt32();
              break;
            }
            case 40: {
              bitField0_ |= 0x00000010;
              partitionLength_ = input.readInt32();
              break;
            }
            case 50: {
              bitField0_ |= 0x00000020;
              data_ = input.readBytes();
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
      return OpenFile.internal_static_OpenFileMessage_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return OpenFile.internal_static_OpenFileMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              OpenFileMessage.class, Builder.class);
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

    public static final int CONTENTLENGTH_FIELD_NUMBER = 2;
    private long contentLength_;
    /**
     * <code>required int64 contentLength = 2;</code>
     */
    public boolean hasContentLength() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required int64 contentLength = 2;</code>
     */
    public long getContentLength() {
      return contentLength_;
    }

    public static final int PARTITIONS_FIELD_NUMBER = 3;
    private int partitions_;
    /**
     * <code>required int32 partitions = 3;</code>
     */
    public boolean hasPartitions() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required int32 partitions = 3;</code>
     */
    public int getPartitions() {
      return partitions_;
    }

    public static final int PARTITIONNO_FIELD_NUMBER = 4;
    private int partitionNo_;
    /**
     * <code>required int32 partitionNo = 4;</code>
     */
    public boolean hasPartitionNo() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>required int32 partitionNo = 4;</code>
     */
    public int getPartitionNo() {
      return partitionNo_;
    }

    public static final int PARTITIONLENGTH_FIELD_NUMBER = 5;
    private int partitionLength_;
    /**
     * <code>required int32 partitionLength = 5;</code>
     */
    public boolean hasPartitionLength() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    /**
     * <code>required int32 partitionLength = 5;</code>
     */
    public int getPartitionLength() {
      return partitionLength_;
    }

    public static final int DATA_FIELD_NUMBER = 6;
    private com.google.protobuf.ByteString data_;
    /**
     * <code>required bytes data = 6;</code>
     */
    public boolean hasData() {
      return ((bitField0_ & 0x00000020) == 0x00000020);
    }
    /**
     * <code>required bytes data = 6;</code>
     */
    public com.google.protobuf.ByteString getData() {
      return data_;
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
      if (!hasContentLength()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasPartitions()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasPartitionNo()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasPartitionLength()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasData()) {
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
        output.writeInt64(2, contentLength_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt32(3, partitions_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeInt32(4, partitionNo_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        output.writeInt32(5, partitionLength_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        output.writeBytes(6, data_);
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
          .computeInt64Size(2, contentLength_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, partitions_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, partitionNo_);
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, partitionLength_);
      }
      if (((bitField0_ & 0x00000020) == 0x00000020)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(6, data_);
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
      if (!(obj instanceof OpenFileMessage)) {
        return super.equals(obj);
      }
      OpenFileMessage other = (OpenFileMessage) obj;

      boolean result = true;
      result = result && (hasPath() == other.hasPath());
      if (hasPath()) {
        result = result && getPath()
            .equals(other.getPath());
      }
      result = result && (hasContentLength() == other.hasContentLength());
      if (hasContentLength()) {
        result = result && (getContentLength()
            == other.getContentLength());
      }
      result = result && (hasPartitions() == other.hasPartitions());
      if (hasPartitions()) {
        result = result && (getPartitions()
            == other.getPartitions());
      }
      result = result && (hasPartitionNo() == other.hasPartitionNo());
      if (hasPartitionNo()) {
        result = result && (getPartitionNo()
            == other.getPartitionNo());
      }
      result = result && (hasPartitionLength() == other.hasPartitionLength());
      if (hasPartitionLength()) {
        result = result && (getPartitionLength()
            == other.getPartitionLength());
      }
      result = result && (hasData() == other.hasData());
      if (hasData()) {
        result = result && getData()
            .equals(other.getData());
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
      if (hasContentLength()) {
        hash = (37 * hash) + CONTENTLENGTH_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getContentLength());
      }
      if (hasPartitions()) {
        hash = (37 * hash) + PARTITIONS_FIELD_NUMBER;
        hash = (53 * hash) + getPartitions();
      }
      if (hasPartitionNo()) {
        hash = (37 * hash) + PARTITIONNO_FIELD_NUMBER;
        hash = (53 * hash) + getPartitionNo();
      }
      if (hasPartitionLength()) {
        hash = (37 * hash) + PARTITIONLENGTH_FIELD_NUMBER;
        hash = (53 * hash) + getPartitionLength();
      }
      if (hasData()) {
        hash = (37 * hash) + DATA_FIELD_NUMBER;
        hash = (53 * hash) + getData().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static OpenFileMessage parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static OpenFileMessage parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static OpenFileMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static OpenFileMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static OpenFileMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static OpenFileMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static OpenFileMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static OpenFileMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static OpenFileMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static OpenFileMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static OpenFileMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static OpenFileMessage parseFrom(
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
    public static Builder newBuilder(OpenFileMessage prototype) {
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
     * Protobuf type {@code OpenFileMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:OpenFileMessage)
        OpenFileMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return OpenFile.internal_static_OpenFileMessage_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return OpenFile.internal_static_OpenFileMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                OpenFileMessage.class, Builder.class);
      }

      // Construct using com.github.sioncheng.prtl.outer.OpenFile.OpenFileMessage.newBuilder()
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
        contentLength_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        partitions_ = 0;
        bitField0_ = (bitField0_ & ~0x00000004);
        partitionNo_ = 0;
        bitField0_ = (bitField0_ & ~0x00000008);
        partitionLength_ = 0;
        bitField0_ = (bitField0_ & ~0x00000010);
        data_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000020);
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return OpenFile.internal_static_OpenFileMessage_descriptor;
      }

      public OpenFileMessage getDefaultInstanceForType() {
        return OpenFileMessage.getDefaultInstance();
      }

      public OpenFileMessage build() {
        OpenFileMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public OpenFileMessage buildPartial() {
        OpenFileMessage result = new OpenFileMessage(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.path_ = path_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.contentLength_ = contentLength_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.partitions_ = partitions_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.partitionNo_ = partitionNo_;
        if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
          to_bitField0_ |= 0x00000010;
        }
        result.partitionLength_ = partitionLength_;
        if (((from_bitField0_ & 0x00000020) == 0x00000020)) {
          to_bitField0_ |= 0x00000020;
        }
        result.data_ = data_;
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
        if (other instanceof OpenFileMessage) {
          return mergeFrom((OpenFileMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(OpenFileMessage other) {
        if (other == OpenFileMessage.getDefaultInstance()) return this;
        if (other.hasPath()) {
          bitField0_ |= 0x00000001;
          path_ = other.path_;
          onChanged();
        }
        if (other.hasContentLength()) {
          setContentLength(other.getContentLength());
        }
        if (other.hasPartitions()) {
          setPartitions(other.getPartitions());
        }
        if (other.hasPartitionNo()) {
          setPartitionNo(other.getPartitionNo());
        }
        if (other.hasPartitionLength()) {
          setPartitionLength(other.getPartitionLength());
        }
        if (other.hasData()) {
          setData(other.getData());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        if (!hasPath()) {
          return false;
        }
        if (!hasContentLength()) {
          return false;
        }
        if (!hasPartitions()) {
          return false;
        }
        if (!hasPartitionNo()) {
          return false;
        }
        if (!hasPartitionLength()) {
          return false;
        }
        if (!hasData()) {
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        OpenFileMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (OpenFileMessage) e.getUnfinishedMessage();
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

      private long contentLength_ ;
      /**
       * <code>required int64 contentLength = 2;</code>
       */
      public boolean hasContentLength() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required int64 contentLength = 2;</code>
       */
      public long getContentLength() {
        return contentLength_;
      }
      /**
       * <code>required int64 contentLength = 2;</code>
       */
      public Builder setContentLength(long value) {
        bitField0_ |= 0x00000002;
        contentLength_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 contentLength = 2;</code>
       */
      public Builder clearContentLength() {
        bitField0_ = (bitField0_ & ~0x00000002);
        contentLength_ = 0L;
        onChanged();
        return this;
      }

      private int partitions_ ;
      /**
       * <code>required int32 partitions = 3;</code>
       */
      public boolean hasPartitions() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required int32 partitions = 3;</code>
       */
      public int getPartitions() {
        return partitions_;
      }
      /**
       * <code>required int32 partitions = 3;</code>
       */
      public Builder setPartitions(int value) {
        bitField0_ |= 0x00000004;
        partitions_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 partitions = 3;</code>
       */
      public Builder clearPartitions() {
        bitField0_ = (bitField0_ & ~0x00000004);
        partitions_ = 0;
        onChanged();
        return this;
      }

      private int partitionNo_ ;
      /**
       * <code>required int32 partitionNo = 4;</code>
       */
      public boolean hasPartitionNo() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>required int32 partitionNo = 4;</code>
       */
      public int getPartitionNo() {
        return partitionNo_;
      }
      /**
       * <code>required int32 partitionNo = 4;</code>
       */
      public Builder setPartitionNo(int value) {
        bitField0_ |= 0x00000008;
        partitionNo_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 partitionNo = 4;</code>
       */
      public Builder clearPartitionNo() {
        bitField0_ = (bitField0_ & ~0x00000008);
        partitionNo_ = 0;
        onChanged();
        return this;
      }

      private int partitionLength_ ;
      /**
       * <code>required int32 partitionLength = 5;</code>
       */
      public boolean hasPartitionLength() {
        return ((bitField0_ & 0x00000010) == 0x00000010);
      }
      /**
       * <code>required int32 partitionLength = 5;</code>
       */
      public int getPartitionLength() {
        return partitionLength_;
      }
      /**
       * <code>required int32 partitionLength = 5;</code>
       */
      public Builder setPartitionLength(int value) {
        bitField0_ |= 0x00000010;
        partitionLength_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 partitionLength = 5;</code>
       */
      public Builder clearPartitionLength() {
        bitField0_ = (bitField0_ & ~0x00000010);
        partitionLength_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>required bytes data = 6;</code>
       */
      public boolean hasData() {
        return ((bitField0_ & 0x00000020) == 0x00000020);
      }
      /**
       * <code>required bytes data = 6;</code>
       */
      public com.google.protobuf.ByteString getData() {
        return data_;
      }
      /**
       * <code>required bytes data = 6;</code>
       */
      public Builder setData(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000020;
        data_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bytes data = 6;</code>
       */
      public Builder clearData() {
        bitField0_ = (bitField0_ & ~0x00000020);
        data_ = getDefaultInstance().getData();
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


      // @@protoc_insertion_point(builder_scope:OpenFileMessage)
    }

    // @@protoc_insertion_point(class_scope:OpenFileMessage)
    private static final OpenFileMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new OpenFileMessage();
    }

    public static OpenFileMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    @Deprecated public static final com.google.protobuf.Parser<OpenFileMessage>
        PARSER = new com.google.protobuf.AbstractParser<OpenFileMessage>() {
      public OpenFileMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new OpenFileMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<OpenFileMessage> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<OpenFileMessage> getParserForType() {
      return PARSER;
    }

    public OpenFileMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_OpenFileMessage_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_OpenFileMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\016OpenFile.proto\"\206\001\n\017OpenFileMessage\022\014\n\004" +
      "path\030\001 \002(\t\022\025\n\rcontentLength\030\002 \002(\003\022\022\n\npar" +
      "titions\030\003 \002(\005\022\023\n\013partitionNo\030\004 \002(\005\022\027\n\017pa" +
      "rtitionLength\030\005 \002(\005\022\014\n\004data\030\006 \002(\014B+\n\037com" +
      ".github.sioncheng.prtl.outerB\010OpenFile"
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
    internal_static_OpenFileMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_OpenFileMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_OpenFileMessage_descriptor,
        new String[] { "Path", "ContentLength", "Partitions", "PartitionNo", "PartitionLength", "Data", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}

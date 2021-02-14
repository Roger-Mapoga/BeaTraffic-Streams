/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package co.za.gmapssolutions.beatraffic;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class Output extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -8006018119412837848L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Output\",\"namespace\":\"co.za.gmapssolutions.beatraffic\",\"fields\":[{\"name\":\"user_id\",\"type\":\"long\",\"doc\":\"user id\"},{\"name\":\"latitude\",\"type\":[\"null\",\"double\"],\"doc\":\"latitude\",\"default\":null},{\"name\":\"longitude\",\"type\":[\"null\",\"double\"],\"doc\":\"longitude\",\"default\":null}],\"version\":\"1\"}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Output> ENCODER =
      new BinaryMessageEncoder<Output>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Output> DECODER =
      new BinaryMessageDecoder<Output>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<Output> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<Output> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<Output> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Output>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this Output to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a Output from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a Output instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static Output fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  /** user id */
   private long user_id;
  /** latitude */
   private java.lang.Double latitude;
  /** longitude */
   private java.lang.Double longitude;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Output() {}

  /**
   * All-args constructor.
   * @param user_id user id
   * @param latitude latitude
   * @param longitude longitude
   */
  public Output(java.lang.Long user_id, java.lang.Double latitude, java.lang.Double longitude) {
    this.user_id = user_id;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return user_id;
    case 1: return latitude;
    case 2: return longitude;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: user_id = (java.lang.Long)value$; break;
    case 1: latitude = (java.lang.Double)value$; break;
    case 2: longitude = (java.lang.Double)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'user_id' field.
   * @return user id
   */
  public long getUserId() {
    return user_id;
  }



  /**
   * Gets the value of the 'latitude' field.
   * @return latitude
   */
  public java.lang.Double getLatitude() {
    return latitude;
  }



  /**
   * Gets the value of the 'longitude' field.
   * @return longitude
   */
  public java.lang.Double getLongitude() {
    return longitude;
  }



  /**
   * Creates a new Output RecordBuilder.
   * @return A new Output RecordBuilder
   */
  public static co.za.gmapssolutions.beatraffic.Output.Builder newBuilder() {
    return new co.za.gmapssolutions.beatraffic.Output.Builder();
  }

  /**
   * Creates a new Output RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Output RecordBuilder
   */
  public static co.za.gmapssolutions.beatraffic.Output.Builder newBuilder(co.za.gmapssolutions.beatraffic.Output.Builder other) {
    if (other == null) {
      return new co.za.gmapssolutions.beatraffic.Output.Builder();
    } else {
      return new co.za.gmapssolutions.beatraffic.Output.Builder(other);
    }
  }

  /**
   * Creates a new Output RecordBuilder by copying an existing Output instance.
   * @param other The existing instance to copy.
   * @return A new Output RecordBuilder
   */
  public static co.za.gmapssolutions.beatraffic.Output.Builder newBuilder(co.za.gmapssolutions.beatraffic.Output other) {
    if (other == null) {
      return new co.za.gmapssolutions.beatraffic.Output.Builder();
    } else {
      return new co.za.gmapssolutions.beatraffic.Output.Builder(other);
    }
  }

  /**
   * RecordBuilder for Output instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Output>
    implements org.apache.avro.data.RecordBuilder<Output> {

    /** user id */
    private long user_id;
    /** latitude */
    private java.lang.Double latitude;
    /** longitude */
    private java.lang.Double longitude;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(co.za.gmapssolutions.beatraffic.Output.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.user_id)) {
        this.user_id = data().deepCopy(fields()[0].schema(), other.user_id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.latitude)) {
        this.latitude = data().deepCopy(fields()[1].schema(), other.latitude);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.longitude)) {
        this.longitude = data().deepCopy(fields()[2].schema(), other.longitude);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing Output instance
     * @param other The existing instance to copy.
     */
    private Builder(co.za.gmapssolutions.beatraffic.Output other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.user_id)) {
        this.user_id = data().deepCopy(fields()[0].schema(), other.user_id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.latitude)) {
        this.latitude = data().deepCopy(fields()[1].schema(), other.latitude);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.longitude)) {
        this.longitude = data().deepCopy(fields()[2].schema(), other.longitude);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'user_id' field.
      * user id
      * @return The value.
      */
    public long getUserId() {
      return user_id;
    }


    /**
      * Sets the value of the 'user_id' field.
      * user id
      * @param value The value of 'user_id'.
      * @return This builder.
      */
    public co.za.gmapssolutions.beatraffic.Output.Builder setUserId(long value) {
      validate(fields()[0], value);
      this.user_id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'user_id' field has been set.
      * user id
      * @return True if the 'user_id' field has been set, false otherwise.
      */
    public boolean hasUserId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'user_id' field.
      * user id
      * @return This builder.
      */
    public co.za.gmapssolutions.beatraffic.Output.Builder clearUserId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'latitude' field.
      * latitude
      * @return The value.
      */
    public java.lang.Double getLatitude() {
      return latitude;
    }


    /**
      * Sets the value of the 'latitude' field.
      * latitude
      * @param value The value of 'latitude'.
      * @return This builder.
      */
    public co.za.gmapssolutions.beatraffic.Output.Builder setLatitude(java.lang.Double value) {
      validate(fields()[1], value);
      this.latitude = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'latitude' field has been set.
      * latitude
      * @return True if the 'latitude' field has been set, false otherwise.
      */
    public boolean hasLatitude() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'latitude' field.
      * latitude
      * @return This builder.
      */
    public co.za.gmapssolutions.beatraffic.Output.Builder clearLatitude() {
      latitude = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'longitude' field.
      * longitude
      * @return The value.
      */
    public java.lang.Double getLongitude() {
      return longitude;
    }


    /**
      * Sets the value of the 'longitude' field.
      * longitude
      * @param value The value of 'longitude'.
      * @return This builder.
      */
    public co.za.gmapssolutions.beatraffic.Output.Builder setLongitude(java.lang.Double value) {
      validate(fields()[2], value);
      this.longitude = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'longitude' field has been set.
      * longitude
      * @return True if the 'longitude' field has been set, false otherwise.
      */
    public boolean hasLongitude() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'longitude' field.
      * longitude
      * @return This builder.
      */
    public co.za.gmapssolutions.beatraffic.Output.Builder clearLongitude() {
      longitude = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Output build() {
      try {
        Output record = new Output();
        record.user_id = fieldSetFlags()[0] ? this.user_id : (java.lang.Long) defaultValue(fields()[0]);
        record.latitude = fieldSetFlags()[1] ? this.latitude : (java.lang.Double) defaultValue(fields()[1]);
        record.longitude = fieldSetFlags()[2] ? this.longitude : (java.lang.Double) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Output>
    WRITER$ = (org.apache.avro.io.DatumWriter<Output>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Output>
    READER$ = (org.apache.avro.io.DatumReader<Output>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeLong(this.user_id);

    if (this.latitude == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeDouble(this.latitude);
    }

    if (this.longitude == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeDouble(this.longitude);
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.user_id = in.readLong();

      if (in.readIndex() != 1) {
        in.readNull();
        this.latitude = null;
      } else {
        this.latitude = in.readDouble();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.longitude = null;
      } else {
        this.longitude = in.readDouble();
      }

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.user_id = in.readLong();
          break;

        case 1:
          if (in.readIndex() != 1) {
            in.readNull();
            this.latitude = null;
          } else {
            this.latitude = in.readDouble();
          }
          break;

        case 2:
          if (in.readIndex() != 1) {
            in.readNull();
            this.longitude = null;
          } else {
            this.longitude = in.readDouble();
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}











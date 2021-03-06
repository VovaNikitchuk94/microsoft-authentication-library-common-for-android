/*
 * KeyVaultClient
 * The key vault client performs cryptographic key operations and vault operations against the Key Vault service.
 *
 * OpenAPI spec version: 2016-10-01
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.microsoft.identity.internal.test.keyvault.model;

import java.util.Objects;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * As of http://tools.ietf.org/html/draft-ietf-jose-json-web-key-18
 */
@ApiModel(description = "As of http://tools.ietf.org/html/draft-ietf-jose-json-web-key-18")

public class JsonWebKey {
  @SerializedName("kid")
  private String kid = null;

  /**
   * JsonWebKey key type (kty).
   */
  @JsonAdapter(KtyEnum.Adapter.class)
  public enum KtyEnum {
    EC("EC"),
    
    EC_HSM("EC-HSM"),
    
    RSA("RSA"),
    
    RSA_HSM("RSA-HSM"),
    
    OCT("oct");

    private String value;

    KtyEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static KtyEnum fromValue(String text) {
      for (KtyEnum b : KtyEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<KtyEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final KtyEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public KtyEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return KtyEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("kty")
  private KtyEnum kty = null;

  @SerializedName("key_ops")
  private List<String> keyOps = null;

  @SerializedName("n")
  private String n = null;

  @SerializedName("e")
  private String e = null;

  @SerializedName("d")
  private String d = null;

  @SerializedName("dp")
  private String dp = null;

  @SerializedName("dq")
  private String dq = null;

  @SerializedName("qi")
  private String qi = null;

  @SerializedName("p")
  private String p = null;

  @SerializedName("q")
  private String q = null;

  @SerializedName("k")
  private String k = null;

  @SerializedName("key_hsm")
  private String keyHsm = null;

  /**
   * Elliptic curve name. For valid values, see JsonWebKeyCurveName.
   */
  @JsonAdapter(CrvEnum.Adapter.class)
  public enum CrvEnum {
    P_256("P-256"),
    
    P_384("P-384"),
    
    P_521("P-521"),
    
    SECP256K1("SECP256K1");

    private String value;

    CrvEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static CrvEnum fromValue(String text) {
      for (CrvEnum b : CrvEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<CrvEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final CrvEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public CrvEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return CrvEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("crv")
  private CrvEnum crv = null;

  @SerializedName("x")
  private String x = null;

  @SerializedName("y")
  private String y = null;

  public JsonWebKey kid(String kid) {
    this.kid = kid;
    return this;
  }

   /**
   * Key identifier.
   * @return kid
  **/
  @ApiModelProperty(value = "Key identifier.")
  public String getKid() {
    return kid;
  }

  public void setKid(String kid) {
    this.kid = kid;
  }

  public JsonWebKey kty(KtyEnum kty) {
    this.kty = kty;
    return this;
  }

   /**
   * JsonWebKey key type (kty).
   * @return kty
  **/
  @ApiModelProperty(value = "JsonWebKey key type (kty).")
  public KtyEnum getKty() {
    return kty;
  }

  public void setKty(KtyEnum kty) {
    this.kty = kty;
  }

  public JsonWebKey keyOps(List<String> keyOps) {
    this.keyOps = keyOps;
    return this;
  }

  public JsonWebKey addKeyOpsItem(String keyOpsItem) {
    if (this.keyOps == null) {
      this.keyOps = new ArrayList<String>();
    }
    this.keyOps.add(keyOpsItem);
    return this;
  }

   /**
   * Get keyOps
   * @return keyOps
  **/
  @ApiModelProperty(value = "")
  public List<String> getKeyOps() {
    return keyOps;
  }

  public void setKeyOps(List<String> keyOps) {
    this.keyOps = keyOps;
  }

  public JsonWebKey n(String n) {
    this.n = n;
    return this;
  }

   /**
   * RSA modulus.
   * @return n
  **/
  @ApiModelProperty(value = "RSA modulus.")
  public String getN() {
    return n;
  }

  public void setN(String n) {
    this.n = n;
  }

  public JsonWebKey e(String e) {
    this.e = e;
    return this;
  }

   /**
   * RSA public exponent.
   * @return e
  **/
  @ApiModelProperty(value = "RSA public exponent.")
  public String getE() {
    return e;
  }

  public void setE(String e) {
    this.e = e;
  }

  public JsonWebKey d(String d) {
    this.d = d;
    return this;
  }

   /**
   * RSA private exponent, or the D component of an EC private key.
   * @return d
  **/
  @ApiModelProperty(value = "RSA private exponent, or the D component of an EC private key.")
  public String getD() {
    return d;
  }

  public void setD(String d) {
    this.d = d;
  }

  public JsonWebKey dp(String dp) {
    this.dp = dp;
    return this;
  }

   /**
   * RSA private key parameter.
   * @return dp
  **/
  @ApiModelProperty(value = "RSA private key parameter.")
  public String getDp() {
    return dp;
  }

  public void setDp(String dp) {
    this.dp = dp;
  }

  public JsonWebKey dq(String dq) {
    this.dq = dq;
    return this;
  }

   /**
   * RSA private key parameter.
   * @return dq
  **/
  @ApiModelProperty(value = "RSA private key parameter.")
  public String getDq() {
    return dq;
  }

  public void setDq(String dq) {
    this.dq = dq;
  }

  public JsonWebKey qi(String qi) {
    this.qi = qi;
    return this;
  }

   /**
   * RSA private key parameter.
   * @return qi
  **/
  @ApiModelProperty(value = "RSA private key parameter.")
  public String getQi() {
    return qi;
  }

  public void setQi(String qi) {
    this.qi = qi;
  }

  public JsonWebKey p(String p) {
    this.p = p;
    return this;
  }

   /**
   * RSA secret prime.
   * @return p
  **/
  @ApiModelProperty(value = "RSA secret prime.")
  public String getP() {
    return p;
  }

  public void setP(String p) {
    this.p = p;
  }

  public JsonWebKey q(String q) {
    this.q = q;
    return this;
  }

   /**
   * RSA secret prime, with p &lt; q.
   * @return q
  **/
  @ApiModelProperty(value = "RSA secret prime, with p < q.")
  public String getQ() {
    return q;
  }

  public void setQ(String q) {
    this.q = q;
  }

  public JsonWebKey k(String k) {
    this.k = k;
    return this;
  }

   /**
   * Symmetric key.
   * @return k
  **/
  @ApiModelProperty(value = "Symmetric key.")
  public String getK() {
    return k;
  }

  public void setK(String k) {
    this.k = k;
  }

  public JsonWebKey keyHsm(String keyHsm) {
    this.keyHsm = keyHsm;
    return this;
  }

   /**
   * HSM Token, used with &#39;Bring Your Own Key&#39;.
   * @return keyHsm
  **/
  @ApiModelProperty(value = "HSM Token, used with 'Bring Your Own Key'.")
  public String getKeyHsm() {
    return keyHsm;
  }

  public void setKeyHsm(String keyHsm) {
    this.keyHsm = keyHsm;
  }

  public JsonWebKey crv(CrvEnum crv) {
    this.crv = crv;
    return this;
  }

   /**
   * Elliptic curve name. For valid values, see JsonWebKeyCurveName.
   * @return crv
  **/
  @ApiModelProperty(value = "Elliptic curve name. For valid values, see JsonWebKeyCurveName.")
  public CrvEnum getCrv() {
    return crv;
  }

  public void setCrv(CrvEnum crv) {
    this.crv = crv;
  }

  public JsonWebKey x(String x) {
    this.x = x;
    return this;
  }

   /**
   * X component of an EC public key.
   * @return x
  **/
  @ApiModelProperty(value = "X component of an EC public key.")
  public String getX() {
    return x;
  }

  public void setX(String x) {
    this.x = x;
  }

  public JsonWebKey y(String y) {
    this.y = y;
    return this;
  }

   /**
   * Y component of an EC public key.
   * @return y
  **/
  @ApiModelProperty(value = "Y component of an EC public key.")
  public String getY() {
    return y;
  }

  public void setY(String y) {
    this.y = y;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JsonWebKey jsonWebKey = (JsonWebKey) o;
    return Objects.equals(this.kid, jsonWebKey.kid) &&
        Objects.equals(this.kty, jsonWebKey.kty) &&
        Objects.equals(this.keyOps, jsonWebKey.keyOps) &&
        Objects.equals(this.n, jsonWebKey.n) &&
        Objects.equals(this.e, jsonWebKey.e) &&
        Objects.equals(this.d, jsonWebKey.d) &&
        Objects.equals(this.dp, jsonWebKey.dp) &&
        Objects.equals(this.dq, jsonWebKey.dq) &&
        Objects.equals(this.qi, jsonWebKey.qi) &&
        Objects.equals(this.p, jsonWebKey.p) &&
        Objects.equals(this.q, jsonWebKey.q) &&
        Objects.equals(this.k, jsonWebKey.k) &&
        Objects.equals(this.keyHsm, jsonWebKey.keyHsm) &&
        Objects.equals(this.crv, jsonWebKey.crv) &&
        Objects.equals(this.x, jsonWebKey.x) &&
        Objects.equals(this.y, jsonWebKey.y);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kid, kty, keyOps, n, e, d, dp, dq, qi, p, q, k, keyHsm, crv, x, y);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JsonWebKey {\n");
    
    sb.append("    kid: ").append(toIndentedString(kid)).append("\n");
    sb.append("    kty: ").append(toIndentedString(kty)).append("\n");
    sb.append("    keyOps: ").append(toIndentedString(keyOps)).append("\n");
    sb.append("    n: ").append(toIndentedString(n)).append("\n");
    sb.append("    e: ").append(toIndentedString(e)).append("\n");
    sb.append("    d: ").append(toIndentedString(d)).append("\n");
    sb.append("    dp: ").append(toIndentedString(dp)).append("\n");
    sb.append("    dq: ").append(toIndentedString(dq)).append("\n");
    sb.append("    qi: ").append(toIndentedString(qi)).append("\n");
    sb.append("    p: ").append(toIndentedString(p)).append("\n");
    sb.append("    q: ").append(toIndentedString(q)).append("\n");
    sb.append("    k: ").append(toIndentedString(k)).append("\n");
    sb.append("    keyHsm: ").append(toIndentedString(keyHsm)).append("\n");
    sb.append("    crv: ").append(toIndentedString(crv)).append("\n");
    sb.append("    x: ").append(toIndentedString(x)).append("\n");
    sb.append("    y: ").append(toIndentedString(y)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}


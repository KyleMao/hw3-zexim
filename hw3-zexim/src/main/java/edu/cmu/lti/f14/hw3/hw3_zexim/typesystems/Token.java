

/* First created by JCasGen Fri Oct 11 01:58:03 EDT 2013 */
package edu.cmu.lti.f14.hw3.hw3_zexim.typesystems;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sat Oct 18 17:55:00 EDT 2014
 * XML source: /home/kylemao/git/hw3-zexim/hw3-zexim/src/main/resources/descriptors/typesystems/VectorSpaceTypes.xml
 * @generated */
public class Token extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Token.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Token() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Token(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Token(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Token(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets 
   * @generated
   * @return value of the feature 
   */
  public String getText() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.f14.hw3.hw3_zexim.typesystems.Token");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Token_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setText(String v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.f14.hw3.hw3_zexim.typesystems.Token");
    jcasType.ll_cas.ll_setStringValue(addr, ((Token_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: frequency

  /** getter for frequency - gets 
   * @generated
   * @return value of the feature 
   */
  public int getFrequency() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_frequency == null)
      jcasType.jcas.throwFeatMissing("frequency", "edu.cmu.lti.f14.hw3.hw3_zexim.typesystems.Token");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Token_Type)jcasType).casFeatCode_frequency);}
    
  /** setter for frequency - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFrequency(int v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_frequency == null)
      jcasType.jcas.throwFeatMissing("frequency", "edu.cmu.lti.f14.hw3.hw3_zexim.typesystems.Token");
    jcasType.ll_cas.ll_setIntValue(addr, ((Token_Type)jcasType).casFeatCode_frequency, v);}    
  }

    
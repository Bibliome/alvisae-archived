/*
 *
 *      This software is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 *
 *      Copyright Institut National de la Recherche Agronomique, 2010-2011.
 *
 */
package fr.inra.mig_bibliome.stane.shared.data3;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


/**
 * @author fpapazian
 */
public interface TermMember extends TermBasic  {

    public int getMemberType();

    public JsArray<? extends JavaScriptObject> getLinkedTerms();

}

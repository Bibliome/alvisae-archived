/*
 *
 *      This software is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 *
 *      Copyright Institut National de la Recherche Agronomique, 2010-2011.
 *
 */
package fr.inra.mig_bibliome.stane.client.data3;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

/**
 *
 * @author fpapazian
 */
public class DocumentInfoListImpl extends JsArray<DocumentInfoImpl> {

    /**
     * @throws IllegalArgumentException if invalid or not expected JSON 
     */
    public final static DocumentInfoListImpl createFromJSON(String jsonStr) {
        DocumentInfoListImpl result = JsonUtils.safeEval(jsonStr).cast();
        return result;
    }

    protected DocumentInfoListImpl() {
    }

}
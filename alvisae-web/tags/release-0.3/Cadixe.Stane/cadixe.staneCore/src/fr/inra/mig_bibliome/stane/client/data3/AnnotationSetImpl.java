/*
 *
 *      This software is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 *
 *      Copyright Institut National de la Recherche Agronomique, 2010-2012.
 *
 */
package fr.inra.mig_bibliome.stane.client.data3;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import fr.inra.mig_bibliome.stane.shared.data3.Annotation;
import fr.inra.mig_bibliome.stane.shared.data3.AnnotationSet;
import java.util.List;

/**
 *
 * @author fpapazian
 */
public class AnnotationSetImpl extends AnnotationSetCoreImpl implements AnnotationSet {

    protected AnnotationSetImpl() {
    }

    final native AnnotationListImpl _getTextAnnotations() /*-{ return this.text_annotations; }-*/;

    @Override
    public final List<? extends Annotation> getTextAnnotations() {
        return new JsArrayDecorator<AnnotationImpl>(_getTextAnnotations());
    }

    final native AnnotationListImpl _getGroups() /*-{ return this.groups; }-*/;

    @Override
    public final List<? extends Annotation> getGroups() {
        return new JsArrayDecorator<AnnotationImpl>(_getGroups());
    }

    final native AnnotationListImpl _getRelations() /*-{ return this.relations; }-*/;

    @Override
    public final List<? extends Annotation> getRelations() {
        return new JsArrayDecorator<AnnotationImpl>(_getRelations());
    }

    public final void scanAnnotations(AnnotatedTextImpl.AnnotationProcessor processor) {
        AnnotationListImpl atl = _getTextAnnotations();
        for (int a = 0; a < atl.length(); a++) {
            AnnotationImpl annotation = atl.get(a);
            if (!processor.process(this, annotation)) {
                break;
            }
        }
        AnnotationListImpl agl = _getGroups();
        for (int a = 0; a < agl.length(); a++) {
            AnnotationImpl annotation = agl.get(a);
            if (!processor.process(this, annotation)) {
                break;
            }
        }
        AnnotationListImpl arl = _getRelations();
        for (int a = 0; a < arl.length(); a++) {
            AnnotationImpl annotation = arl.get(a);
            if (!processor.process(this, annotation)) {
                break;
            }
        }
    }

    final void addBackRef(final AnnotatedTextImpl annotatedText) {
        scanAnnotations(new AnnotatedTextImpl.AnnotationProcessor() {

            @Override
            public boolean process(AnnotationSetImpl annotationSet, AnnotationImpl annotation) {
                annotation.setAnnotatedText(annotatedText);
                return true;
            }
        });
    }

    final AnnotatedTextImpl removeBackRef() {
        final AnnotatedTextImpl[] annotatedText = {null};

        scanAnnotations(new AnnotatedTextImpl.AnnotationProcessor() {

            @Override
            public boolean process(AnnotationSetImpl annotationSet, AnnotationImpl annotation) {
                annotatedText[0] = annotation.getAnnotatedText();
                annotation.setAnnotatedText(null);
                return true;
            }
        });
        return annotatedText[0];
    }

    @Override
    public final String getJSON() {
        AnnotatedTextImpl annotatedText = null;
        String jsonStr;
        try {
            annotatedText = removeBackRef();
            jsonStr = new JSONObject(this).toString();
        } finally {
            addBackRef(annotatedText);
        }
        return jsonStr;
    }

    @Override
    public final String getCSV() {
        final StringBuilder result = new StringBuilder();

        scanAnnotations(new AnnotatedTextImpl.AnnotationProcessor() {

            @Override
            public boolean process(AnnotationSetImpl annotationSet, AnnotationImpl annotation) {
                result.append(annotation.getCSV());
                return true;
            }
        });
        return result.toString();
    }
    
    final void addTextAnnotation(AnnotationImpl annotation) {
        _getTextAnnotations().push(annotation);
    }

    final void addGroupAnnotation(AnnotationImpl annotation) {
        _getGroups().push(annotation);
    }

    final void addRelationAnnotation(AnnotationImpl annotation) {
        _getRelations().push(annotation);
    }

    private final int getAnnotationIndex(String annotationId, JsArray<AnnotationImpl> array) {
        String id = annotationId.trim();
        for (int i = 0, n = array.length(); i < n; ++i) {
            AnnotationImpl annotation = array.get(i);
            if (annotation.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private final native void _removeTextAnnotation(int index) /*-{ this.text_annotations.splice(index,1); }-*/;

    private final native void _removeGroupAnnotation(int index) /*-{ this.groups.splice(index,1); }-*/;

    private final native void _removeRelationtAnnotation(int index) /*-{ this.relations.splice(index,1); }-*/;

    final void removeTextAnnotation(String annotationId) {
        int index = getAnnotationIndex(annotationId, _getTextAnnotations());
        _removeTextAnnotation(index);
    }

    final void removeGroupAnnotation(String annotationId) {
        int index = getAnnotationIndex(annotationId, _getGroups());
        _removeGroupAnnotation(index);
    }

    final void removeRelationAnnotation(String annotationId) {
        int index = getAnnotationIndex(annotationId, _getRelations());
        _removeRelationtAnnotation(index);
    }

    final boolean isTextAnnotationFound(String annotationId) {
        return -1 != getAnnotationIndex(annotationId, _getTextAnnotations());
    }
}

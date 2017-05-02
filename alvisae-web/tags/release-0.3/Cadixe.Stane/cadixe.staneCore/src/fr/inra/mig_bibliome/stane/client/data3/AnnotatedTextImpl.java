/*
 *
 *      This software is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 *
 *      Copyright Institut National de la Recherche Agronomique, 2010-2012.
 *
 */
package fr.inra.mig_bibliome.stane.client.data3;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONObject;
import fr.inra.mig_bibliome.stane.shared.data3.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author fpapazian
 */
public class AnnotatedTextImpl extends JavaScriptObject implements AnnotatedText {

    private static final String AnnotationIdPrefix = "ann-";

    private static interface AnnotationFilter {

        public boolean accept(Annotation annotation);
    }

    static interface AnnotationProcessor {

        public boolean process(AnnotationSetImpl annotationSet, AnnotationImpl annotation);
    }

    /**
     * @throws IllegalArgumentException if invalid JSON string or structure not corresponding to AnnotatedText
     */
    public final static AnnotatedTextImpl createFromJSON(String jsonStr) {
        AnnotatedTextImpl result = JsonUtils.safeEval(jsonStr).cast();
        result.addBackRef();
        return result;
    }
    //--------------------------------------------------------------------------

    protected AnnotatedTextImpl() {
    }

    @Override
    public final native Document getDocument() /*-{ return this.document; }-*/;

    @Override
    public final native AnnotationSchemaDefinition getAnnotationSchema() /*-{ return this.schema; }-*/;

    final native AnnotationSetListImpl _getAnnotationSetList() /*-{ return this.annotation; }-*/;

    @Override
    public final List<? extends AnnotationSet> getAnnotationSetList() {
        return new JsArrayDecorator<AnnotationSetImpl>(_getAnnotationSetList());
    }

    final native AnnotationSetInfoListImpl _getAnnotationSetInfoList() /*-{ return this.annotation_sets; }-*/;

    @Override
    public final List<? extends AnnotationSetInfoImpl> getAnnotationSetInfoList() {
        return new JsArrayDecorator<AnnotationSetInfoImpl>(_getAnnotationSetInfoList());
    }

    final void addAdditionalAnnotationSet(AnnotationSetImpl annotationSet) {
        removeBackRef();
        _getAnnotationSetList().addAnnotationSet(annotationSet);
        addBackRef();
    }

    @Override
    public final void scanAnnotations(final AnnotatedText.AnnotationProcessor processor) {
        scanAnnotations(new AnnotatedTextImpl.AnnotationProcessor() {

            @Override
            public boolean process(AnnotationSetImpl annotationSet, AnnotationImpl annotation) {
                return processor.process(annotationSet, annotation);
            }
        });
    }

    public final void scanAnnotations(AnnotatedTextImpl.AnnotationProcessor processor) {
        AnnotationSetListImpl asl = _getAnnotationSetList();
        for (int as = 0; as < asl.length(); as++) {
            AnnotationSetImpl annotationSet = asl.get(as);
            annotationSet.scanAnnotations(processor);
        }
    }

    private final void addBackRef() {
        scanAnnotations(new AnnotatedTextImpl.AnnotationProcessor() {

            @Override
            public boolean process(AnnotationSetImpl annotationSet, AnnotationImpl annotation) {
                annotation.setAnnotatedText(AnnotatedTextImpl.this);
                return true;
            }
        });
    }

    private final AnnotatedTextImpl removeBackRef() {
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
        String jsonStr = null;
        try {
            removeBackRef();
            jsonStr = new JSONObject(this).toString();
        } finally {
            addBackRef();
        }
        return jsonStr;
    }

    @Override
    public final Collection<Annotation> getAnnotations() {
        return getFilteredAnnotations(null);
    }

    @Override
    public final Annotation getAnnotation(final String annotationId) {
        final List<Annotation> liste = new ArrayList<Annotation>();
        scanAnnotations(new AnnotatedTextImpl.AnnotationProcessor() {

            @Override
            public boolean process(AnnotationSetImpl annotationSet, AnnotationImpl annotation) {
                if (annotationId.equals(annotation.getId())) {
                    liste.add(annotation);
                    return false;
                } else {
                    return true;
                }
            }
        });

        return liste.isEmpty() ? null : liste.get(0);
    }

    public final List<Annotation> getFilteredAnnotations(final AnnotationFilter filter) {
        final List<Annotation> liste = new ArrayList<Annotation>();
        scanAnnotations(new AnnotatedTextImpl.AnnotationProcessor() {

            @Override
            public boolean process(AnnotationSetImpl annotationSet, AnnotationImpl annotation) {
                if (filter == null || filter.accept(annotation)) {
                    liste.add(annotation);
                }
                return true;
            }
        });
        return liste;
    }

    @Override
    public final Collection<Annotation> getAnnotations(final String type) {
        if (type == null) {
            throw new NullPointerException("Annotation type should not be null");
        }
        return getFilteredAnnotations(new AnnotationFilter() {

            @Override
            public boolean accept(Annotation annotation) {
                return type.equals(annotation.getAnnotationType());
            }
        });
    }

    @Override
    public final List<Annotation> getAnnotations(final AnnotationKind kind) {
        if (kind == null) {
            throw new NullPointerException("AnnotationKind should not be null");
        }
        return getFilteredAnnotations(new AnnotationFilter() {

            @Override
            public boolean accept(Annotation annotation) {
                return kind.equals(annotation.getAnnotationKind());
            }
        });
    }

    public final AnnotationImpl createLooseTextAnnotation(String id, String type, Collection<Fragment> fragments) {
        AnnotationImpl annotation = AnnotationImpl.create(id, AnnotationKind.TEXT, type);
        annotation.getTextBinding().addFragments(fragments);
        annotation.setAnnotatedText(this);
        return annotation;
    }

    public final AnnotationImpl createLooseGroupAnnotation(String id, String type, Collection<Annotation> components) {
        AnnotationImpl annotation = AnnotationImpl.create(id, AnnotationKind.GROUP, type);
        annotation.getAnnotationGroup().addComponents(components);
        annotation.setAnnotatedText(this);
        return annotation;
    }

    public final AnnotationImpl createLooseRelationAnnotation(String id, String type, Map<String, Annotation> arguments) {
        AnnotationImpl annotation = AnnotationImpl.create(id, AnnotationKind.RELATION, type);
        Relation relation = annotation.getRelation();
        for (Entry<String, Annotation> e : arguments.entrySet()) {
            relation.setArgument(e.getKey(), e.getValue(), true);
        }
        annotation.setAnnotatedText(this);
        return annotation;
    }
}
/*
 *
 *      This software is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 *
 *      Copyright Institut National de la Recherche Agronomique, 2010-2011.
 *
 */
package fr.inra.mig_bibliome.stane.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 * @author fpapazian
 */
public interface StanEditorResources extends ClientBundle {

    public static final StanEditorResources INSTANCE = GWT.create(StanEditorResources.class);

    @Source("images/_edit-vertical-alignment-top.png")
    ImageResource IncLineSizeIcon();

    @Source("images/_edit-vertical-alignment.png")
    ImageResource DecLineSizeIcon();

    @Source("images/tag--plus.png")
    ImageResource AddAnnotIcon();

    @Source("images/tag--minus.png")
    ImageResource DelAnnotIcon();

    @Source("images/tag--pencil.png")
    ImageResource EditAnnotIcon();

    @Source("images/foaf.png")
    ImageResource ShowVisibilityFilterIcon();

    @Source("images/chevron-small-expand-11.png")
    ImageResource ExpandIcon();

    @Source("images/tick-small-11.png")
    ImageResource ApplySmallIcon();

    @Source("images/cross-small-11.png")
    ImageResource CancelSmallIcon();

    @Source("images/plus-small-11.png")
    ImageResource AddSmallIcon();

    @Source("images/minus-small-11.png")
    ImageResource DelSmallIcon();

    @Source("images/magnet-blue.png")
    ImageResource DragHandleIcon();

    @Source("images/arrow-curve-180-left.png")
    ImageResource UndoIcon();

    @Source("images/arrow-curve.png")
    ImageResource RedoIcon();

    @Source("images/layer-shape-curve.png")
    ImageResource RelationIcon();

    @Source("images/ui-text-field--plus.png")
    ImageResource AddTextToAnnotIcon();

    @Source("images/ui-text-field--minus.png")
    ImageResource DelTextFromAnnotIcon();

    @Source("images/chevron-small-expand.png")
    ImageResource PropsExpandIcon();

    @Source("images/clipboard-task.png")
    ImageResource DocValidateIcon();

    @Source("images/layer-shape-line--plus.png")
    ImageResource AddRelationIcon();

    @Source("images/layer-shape-line--minus.png")
    ImageResource DelRelationIcon();

    @Source("images/layer-shape-line--pencil.png")
    ImageResource EditRelationIcon();

    @Source("images/jar--plus.png")
    ImageResource AddGroupIcon();

    @Source("images/jar--minus.png")
    ImageResource DelGroupIcon();

    @Source("images/jar--pencil.png")
    ImageResource EditGroupIcon();

    @Source("images/layer-shape-text.png")
    ImageResource TextSelectModeIcon();

    @Source("images/layer-shape-curve.png")
    ImageResource RelationSelectModeIcon();

    @Source("images/target-bgsmall.gif")
    ImageResource AnimatedDropTargetImg();

    @Source("images/tag.png")
    ImageResource TextAnnotationIcon();

    @Source("images/layer-shape-line.png")
    ImageResource RelationAnnotationIcon();

    @Source("images/jar.png")
    ImageResource GroupAnnotationIcon();

    @Source("images/lightbulb.png")
    ImageResource UnVeiledAnnotationIcon();

    @Source("images/lightbulb_off.png")
    ImageResource VeiledAnnotationIcon();

    @Source("images/disk--arrow.png")
    ImageResource RefreshFileListIcon();

    @Source("images/tick-small-11.png")
    ImageResource CheckedIcon();

    @Source("images/150.png")
    ImageResource ForbiddenDragIcon();

    @Source("images/term-white.png")
    ImageResource TermDragIcon();

    static interface StanEditorGlobalCSS extends CssResource {

        String DroppableHover();

        String UnDroppableHover();

        String DragHelper();
    }

    @Source("images/StanEditorGlobal.css")
    StanEditorGlobalCSS css();
}
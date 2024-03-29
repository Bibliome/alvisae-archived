/*
 *
 *      This software is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 *
 *      Copyright Institut National de la Recherche Agronomique, 2010.
 *
 */

package fr.inra.mig_bibliome.stane.client.Events;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 * @author fpapazian
 */
public interface RangeSelectionChangedEventHandler extends EventHandler {

    void onRangeSelectionChanged(RangeSelectionChangedEvent event);
}

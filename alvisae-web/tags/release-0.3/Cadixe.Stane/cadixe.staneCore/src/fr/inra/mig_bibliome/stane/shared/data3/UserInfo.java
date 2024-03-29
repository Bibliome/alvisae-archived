/*
 *
 *      This software is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 *
 *      Copyright Institut National de la Recherche Agronomique, 2010.
 *
 */
package fr.inra.mig_bibliome.stane.shared.data3;

/**
 *
 * @author fpapazian
 */
public interface UserInfo {

    /**
     *
     * @return the Id of this User
     */
    int getId();

    /**
     *
     * @return the human readable name of the User (=the one used for authentication)
     */
    String getDisplayName();
}

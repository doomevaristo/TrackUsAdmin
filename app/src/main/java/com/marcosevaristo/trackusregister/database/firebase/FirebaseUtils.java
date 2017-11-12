package com.marcosevaristo.trackusregister.database.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.marcosevaristo.trackusregister.utils.StringUtils;

public class FirebaseUtils {

    private static FirebaseDatabase database;

    private static final String NODE_MUNICIPIOS = "municipios";
    private static final String NODE_LINHAS = "linhas";

    private static FirebaseDatabase getDatabase() {
        if(database == null) {
            database = FirebaseDatabase.getInstance();
        }
        return database;
    }

    public static DatabaseReference getLinhasReference(Long municipioID, String numeroLinha) {
        DatabaseReference databaseReferenceLinhas = null;
        if(municipioID != null) {
            databaseReferenceLinhas = getDatabase().getReference().child(NODE_MUNICIPIOS)
                    .child(municipioID.toString()).child(NODE_LINHAS);
            if(StringUtils.isNotBlank(numeroLinha)) {
                databaseReferenceLinhas = databaseReferenceLinhas.child(numeroLinha);
            }
        }
        return databaseReferenceLinhas;
    }

    public static DatabaseReference getMunicipiosReference(Long municipioID) {
        DatabaseReference databaseReferenceMunicipios = getDatabase().getReference().child(NODE_MUNICIPIOS);
        if(municipioID != null) {
            databaseReferenceMunicipios = databaseReferenceMunicipios.child(municipioID.toString());
        }
        return databaseReferenceMunicipios;
    }
}

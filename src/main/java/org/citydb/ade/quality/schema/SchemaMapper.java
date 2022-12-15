package org.citydb.ade.quality.schema;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public class SchemaMapper {
    private final Map<ADETable, String> tableNames = new EnumMap<>(ADETable.class);
    private final Map<ADESequence, String> sequenceNames = new EnumMap<>(ADESequence.class);

    public void populateSchemaNames(String prefix) {
        for (ADETable table : ADETable.values()) {
            tableNames.put(table, prefix + "_" + table.toString().toLowerCase(Locale.ROOT));
        }

        for (ADESequence sequence : ADESequence.values()) {
            sequenceNames.put(sequence, prefix + "_" + sequence.toString().toLowerCase(Locale.ROOT));
        }
    }

    public String getTableName(ADETable table) {
        return tableNames.get(table);
    }

    public ADETable fromTableName(String tableName) {
        tableName = tableName.toLowerCase();

        for (Map.Entry<ADETable, String> entry : tableNames.entrySet()) {
            if (entry.getValue().equals(tableName))
                return entry.getKey();
        }

        return null;
    }

    public String getSequenceName(ADESequence sequence) {
        return sequenceNames.get(sequence);
    }
}

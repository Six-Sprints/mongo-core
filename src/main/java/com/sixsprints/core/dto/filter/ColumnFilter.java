package com.sixsprints.core.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "filterType", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = NumberColumnFilter.class, name = "NUMBER"),
    @JsonSubTypes.Type(value = SetColumnFilter.class, name = "SET"),
    @JsonSubTypes.Type(value = BooleanColumnFilter.class, name = "BOOLEAN"),
    @JsonSubTypes.Type(value = DateColumnFilter.class, name = "DATE"),
    @JsonSubTypes.Type(value = SearchColumnFilter.class, name = "TEXT"),
    @JsonSubTypes.Type(value = ExactMatchColumnFilter.class, name = "EXACT")})
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public abstract class ColumnFilter {

  ColumnFilterTypeEnum filterType;

}

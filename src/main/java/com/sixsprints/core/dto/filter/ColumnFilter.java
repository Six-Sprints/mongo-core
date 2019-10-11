package com.sixsprints.core.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "filterType")
@JsonSubTypes({ @JsonSubTypes.Type(value = NumberColumnFilter.class, name = "number"),
    @JsonSubTypes.Type(value = SetColumnFilter.class, name = "set"),
    @JsonSubTypes.Type(value = BooleanColumnFilter.class, name = "boolean"),
    @JsonSubTypes.Type(value = DateColumnFilter.class, name = "date"),
    @JsonSubTypes.Type(value = SearchColumnFilter.class, name = "text"),
    @JsonSubTypes.Type(value = ExactMatchColumnFilter.class, name = "exact") })
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public abstract class ColumnFilter {

  String filterType;

}

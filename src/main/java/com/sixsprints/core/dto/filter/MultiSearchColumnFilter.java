package com.sixsprints.core.dto.filter;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MultiSearchColumnFilter extends ColumnFilter{

    private String filter;

    private List<String> fields;

    private boolean slugExcludedFromSearch;

}

package com.airbus.retex.business.dto.childRequest;

import com.airbus.retex.business.dto.common.PaginationDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
public class ChildRequestFilteringDto extends PaginationDto {
    private ChildRequestSortingValues sortBy;
    private Sort.Direction sortDirection;
}

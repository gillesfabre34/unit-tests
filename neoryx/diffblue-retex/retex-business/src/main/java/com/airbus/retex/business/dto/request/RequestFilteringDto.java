package com.airbus.retex.business.dto.request;

import com.airbus.retex.business.dto.common.PaginationDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class RequestFilteringDto extends PaginationDto {
    private String name;
    private List<Long> airbusEntityIds;
    private List <Long> requesterIds;
    private List<Long> originIds;
    private List<EnumStatus> statuses;
    private RequestSortingValues sortBy;
    private Sort.Direction sortDirection;
}

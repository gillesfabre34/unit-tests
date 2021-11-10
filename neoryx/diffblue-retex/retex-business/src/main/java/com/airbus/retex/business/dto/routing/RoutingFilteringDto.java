package com.airbus.retex.business.dto.routing;

import com.airbus.retex.business.dto.IVersionableInDto;
import com.airbus.retex.business.dto.common.PaginationDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RoutingFilteringDto extends PaginationDto implements IVersionableInDto {

    private Long id;
    private String name;
    private String pnOrPnRoot;
    private String partNumber;
    private String partNumberRoot;
    @DateTimeFormat(pattern = "yyyy-MM-dd") //FIXME find global solution
    private LocalDate creationDate;
    private EnumStatus status;


    @Override
    public Long getNaturalId() {
        return this.id;
    }
}

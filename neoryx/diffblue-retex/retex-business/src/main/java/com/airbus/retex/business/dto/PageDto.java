package com.airbus.retex.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageDto<D extends Dto> {

    private List<D> results;

    private Long totalResults;

    private Integer totalPages;

}

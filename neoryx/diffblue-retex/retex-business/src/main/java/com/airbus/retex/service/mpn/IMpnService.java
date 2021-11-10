package com.airbus.retex.service.mpn;

import com.airbus.retex.model.part.Mpn;

import java.util.List;

public interface IMpnService {

   void saveNewMpn(List<Mpn> mpns);
}

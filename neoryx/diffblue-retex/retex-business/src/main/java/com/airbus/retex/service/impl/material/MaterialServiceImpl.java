package com.airbus.retex.service.impl.material;

import com.airbus.retex.model.material.Material;
import com.airbus.retex.persistence.material.MaterialRepository;
import com.airbus.retex.service.material.IMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class MaterialServiceImpl implements IMaterialService {

    @Autowired
    MaterialRepository materialRepository;

    @Override
    public List<String> getAllMaterials() {
        List<Material> materials = materialRepository.findAll();

        return materials.stream().map(Material::getCode).collect(Collectors.toList());
    }

}

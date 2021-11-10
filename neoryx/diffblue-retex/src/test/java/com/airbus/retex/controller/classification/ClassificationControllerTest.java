package com.airbus.retex.controller.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.airbus.retex.configuration.filter.ApiKeyAuthentication;
import com.airbus.retex.model.classification.EnumClassification;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ClassificationControllerTest {
    @Test
    public void testGetClassifications() {
        ClassificationController classificationController = new ClassificationController();
        ResponseEntity<List<EnumClassification>> actualClassifications = classificationController
                .getClassifications(new ApiKeyAuthentication());
        assertEquals("<200 OK OK,[ZHS, ZD, ZC],[]>", actualClassifications.toString());
        assertEquals(HttpStatus.OK, actualClassifications.getStatusCode());
        assertTrue(actualClassifications.hasBody());
    }
}


package com.airbus.retex.model.damage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.airbus.retex.model.media.Media;
import org.junit.jupiter.api.Test;

public class DamageTest {
    @Test
    public void testAddTranslation() {
        Damage damage = new Damage();
        DamageTranslation damageTranslation = new DamageTranslation();
        damage.addTranslation(damageTranslation);
        assertSame(damage, damageTranslation.getEntity());
    }

    @Test
    public void testAddImage() {
        Damage damage = new Damage();
        damage.addImage(new Media());
        assertEquals(1, damage.getImages().size());
    }
}


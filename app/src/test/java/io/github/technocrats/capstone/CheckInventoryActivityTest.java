package io.github.technocrats.capstone;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.technocrats.capstone.models.Subcategory;

import static org.junit.Assert.assertEquals;

public class CheckInventoryActivityTest {

    @Test
    public void checkSubcategoryDuplicates() {
        Subcategory s1 = new Subcategory(1, "SubA", 2.5f);
        Subcategory s2 = new Subcategory(2, "SubB", 4.36f);
        Subcategory s3 = new Subcategory(1, "SubA", 1.5f);
        Subcategory s4 = new Subcategory(3, "SubC", 3.2f);

        List<Subcategory> subcategory = new ArrayList<>();
        subcategory.add(s1);
        subcategory.add(s2);
        subcategory.add(s3);
        subcategory.add(s4);

        CheckInventoryActivity.checkSubcategoryDuplicates(subcategory);

        int listSize = subcategory.size();
        assertEquals(listSize, 3);

        float updatedDupValue = subcategory.get(1).getValue();
        assertEquals(updatedDupValue, 4f, 0.5f);
    }

    @Test
    public void removeFloatElement() {
        float[] array1 = {1.2f, 3.5f, 5.2f, 4.7f};
        float[] array2 = CheckInventoryActivity.removeFloatElement(array1, 2);

        int arraySize = array2.length;
        assertEquals(arraySize, 3);

        float element2 = array2[2];
        assertEquals(element2, 4.7f, 0.5f);
    }
}
package com.dialogs.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.dialogs.model.Company;
import com.dialogs.model.Dialog;

public class CompanyTest {

    @Test
    public void testCompanyHasNoDialogsWithNullList() {
        Company company = new Company();
        company.setDialogs(null);
        assertFalse(company.hasDialogs());
    }

    @Test
    public void testCompanyHasNoDialogsWithEmptyList() {
        Company company = new Company();
        company.setDialogs(Collections.emptyList());
        assertFalse(company.hasDialogs());
    }

    @Test
    public void testCompanyHasDialogsWithNoEmptyList() {
        Company company = new Company();
        List<Dialog> dialogs = new ArrayList<Dialog>();
        dialogs.add(new Dialog());
        company.setDialogs(dialogs);
        assertTrue(company.hasDialogs());
    }

    @Test
    public void testCompanySignUpWithinPeriod() {
        LocalDate start = LocalDate.of(2018, 1, 1);
        LocalDate end = LocalDate.of(2018, 10, 31);
        Company company = new Company();
        company.setSignedUp(LocalDate.of(2018, 1, 15));
        assertTrue(company.isSignUpBetween(start, end));
    }

    @Test
    public void testCompanySignUpStartOfPeriod() {
        LocalDate start = LocalDate.of(2018, 1, 1);
        LocalDate end = LocalDate.of(2018, 10, 31);
        Company company = new Company();
        company.setSignedUp(LocalDate.of(2018, 1, 1));
        assertTrue(company.isSignUpBetween(start, end));
    }

    @Test
    public void testCompanySignUpEndOfPeriod() {
        LocalDate start = LocalDate.of(2018, 1, 1);
        LocalDate end = LocalDate.of(2018, 10, 31);
        Company company = new Company();
        company.setSignedUp(LocalDate.of(2018, 10, 31));
        assertTrue(company.isSignUpBetween(start, end));
    }

    @Test
    public void testCompanySignUpOutsidePeriod() {
        LocalDate start = LocalDate.of(2018, 1, 1);
        LocalDate end = LocalDate.of(2018, 10, 31);
        Company company = new Company();
        company.setSignedUp(LocalDate.of(2018, 11, 1));
        assertFalse(company.isSignUpBetween(start, end));
        company.setSignedUp(LocalDate.of(2017, 12, 31));
        assertFalse(company.isSignUpBetween(start, end));
    }
}

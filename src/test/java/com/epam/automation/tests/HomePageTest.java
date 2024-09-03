package com.epam.automation.tests;

import java.io.File;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import io.github.bonigarcia.wdm.WebDriverManager;

public class HomePageTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void checkPageTitle() {
        driver.get("https://www.epam.com");
        String expectedTitle = "EPAM | Software Engineering & Product Development Services";
        String actualTitle = driver.getTitle();
        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    public void testThemeSwitch() {
        driver.get("https://www.epam.com");
        WebElement themeSwitcher = driver.findElement(By.id("themeSwitcher"));
        String originalTheme = driver.findElement(By.tagName("body")).getAttribute("class");
        themeSwitcher.click();
        String switchedTheme = driver.findElement(By.tagName("body")).getAttribute("class");
        assertNotEquals(originalTheme, switchedTheme);
    }

    @Test
    public void checkLanguageChangeToUkrainian() {
        driver.get("https://www.epam.com");
        WebElement languageMenu = driver.findElement(By.xpath("//button[contains(text(),'Global (English)')]"));
        languageMenu.click();
        WebElement ukrainianLanguageOption = driver.findElement(By.xpath("//a[contains(text(),'Україна (Українська)')]"));
        ukrainianLanguageOption.click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "ВАКАНСІЇ"));
        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("900 ДНІВ ВІЙНИ РАЗОМ З УКРАЇНОЮ"));
    }

    @Test
    public void checkPoliciesList() {
        driver.get("https://www.epam.com");
        WebElement footer = driver.findElement(By.tagName("footer"));
        List<String> policiesList = footer.findElements(By.tagName("a")).stream()
                                         .map(WebElement::getText)
                                         .collect(Collectors.toList());
        assertTrue(policiesList.containsAll(List.of("INVESTORS", "COOKIE POLICY", "OPEN SOURCE", "APPLICANT PRIVACY NOTICE", "PRIVACY POLICY", "WEB ACCESSIBILITY")));
    }

    @Test
    public void checkLocationSwitchByRegion() {
        driver.get("https://www.epam.com");
        WebElement americasTab = driver.findElement(By.xpath("//button[contains(text(), 'AMERICAS')]"));
        WebElement emeaTab = driver.findElement(By.xpath("//button[contains(text(), 'EMEA')]"));
        WebElement apacTab = driver.findElement(By.xpath("//button[contains(text(), 'APAC')]"));

        americasTab.click();
        assertTrue(checkLocations(List.of("CANADA", "COLOMBIA", "DOMINICAN REPUBLIC", "MEXICO")));

        emeaTab.click();
        assertTrue(checkLocations(List.of("ARMENIA", "AUSTRIA", "BELARUS", "BELGIUM")));

        apacTab.click();
        assertTrue(checkLocations(List.of("AUSTRALIA", "CHINA MAINLAND", "HONG KONG SAR", "INDIA")));
    }

    private boolean checkLocations(List<String> expectedLocations) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".locations-list-visible")));
        List<WebElement> locations = driver.findElements(By.cssSelector(".location-list-item"));
        List<String> visibleLocations = locations.stream().map(WebElement::getText).collect(Collectors.toList());
        return visibleLocations.containsAll(expectedLocations);
    }
    @Test
    public void checkSearchFunction() {
        driver.get("https://www.epam.com");
        WebElement searchIcon = driver.findElement(By.cssSelector("button.header-search__button"));
        searchIcon.click();
        WebElement searchInput = driver.findElement(By.cssSelector("input.header-search__input"));
        searchInput.sendKeys("AI");
        searchInput.submit();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results__list")));
        List<WebElement> searchResults = driver.findElements(By.cssSelector(".search-result-item"));
        assertTrue(searchResults.size() > 0, "Search results should be displayed");
    }
    @Test
    public void checkFormsFieldsValidation() {
        driver.get("https://www.epam.com/about/who-we-are/contact");
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();
        
        List<WebElement> requiredFields = driver.findElements(By.cssSelector(".input[required='required']"));
        for (WebElement field : requiredFields) {
            String validity = field.getAttribute("validity");
            assertEquals("false", validity, "Required field should be invalid when empty.");
        }
    }
    @Test
    public void checkCompanyLogoLeadsToHomePage() {
        driver.get("https://www.epam.com/about");
        WebElement companyLogo = driver.findElement(By.cssSelector(".header__logo"));
        companyLogo.click();
        
        String currentUrl = driver.getCurrentUrl();
        assertEquals("https://www.epam.com/", currentUrl, "Clicking the logo should redirect to the home page.");
    }
    @Test
    public void checkDownloadReportFunction() {
        driver.get("https://www.epam.com/about");
        WebElement downloadLink = driver.findElement(By.xpath("//a[contains(@href, 'EPAM-Corporate-Overview-2023.pdf')]"));
        downloadLink.click();
        
        File downloadedFile = new File("C:/Users/Inna_Batsalai/Downloads/EPAM-Corporate-Overview-2023.pdf");
        assertTrue(downloadedFile.exists());
        assertTrue(downloadedFile.getName().endsWith(".pdf"));
        downloadedFile.deleteOnExit();
    }
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
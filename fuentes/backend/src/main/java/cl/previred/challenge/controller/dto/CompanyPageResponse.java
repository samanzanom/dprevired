package cl.previred.challenge.controller.dto;

import java.util.List;

public class CompanyPageResponse {
    private List<CompanyResponse> companies;
    private int currentPage;
    private int totalItems;
    private int totalPages;
    private int size;

    public CompanyPageResponse(List<CompanyResponse> companies, int currentPage, int totalItems, int totalPages, int size) {
        this.companies = companies;
        this.currentPage = currentPage;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.size = size;
    }

    // Getters y setters

    public List<CompanyResponse> getCompanies() {
        return companies;
    }

    public void setCompanies(List<CompanyResponse> companies) {
        this.companies = companies;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
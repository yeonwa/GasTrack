# Implementation Plan - Update Dashboard & Fix Products Render Issue

This plan covers updating the Employee Dashboard with the requested sections (Sales Performance, No. of Transactions, Stock Attention, Best Seller, Sales Trend Graph, Restock Suggestion) and resolving the layout render issue in the Products screen.

## Proposed Changes

### [MODIFY] [Dashboard(Employee).kt](file:///D:/COLLEGE/CAPSTONE NEW/GasTrack/app/src/main/java/com/example/gastrack/ui/presentation/Dashboard(Employee).kt)
- Expand the stats section to include **Sales Performance** and **No. of Transactions**.
- Add a custom **Sales Trend Graph** component using basic Compose `Canvas` or stylized layouts.
- Add a **Best Seller** highlight card.
- Add a **Stock Attention & Restock Suggestion** list to provide actionable data for the employee.

### [MODIFY] [Products(Employee).kt](file:///D:/COLLEGE/CAPSTONE NEW/GasTrack/app/src/main/java/com/example/gastrack/ui/presentation/Products(Employee).kt)
- Resolve the `NoSuchElementException` in the Compose Preview by refactoring the `Scaffold` wrapper into a safe `Box` layout with background colors, ensuring preview stability across different compiler versions.

## Verification Plan

### Automated Tests
- Trigger `app:compileDebugKotlin` to ensure no syntax or type errors.

### Manual Verification
- Render Compose Previews for `DashboardEmployeeScreenPreview` and `ProductsEmployeeScreenPreview` to verify pixel-perfect design.

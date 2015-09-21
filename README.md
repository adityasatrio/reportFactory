reportFactory
=============

- generate jasper report, this source only wrapping jasper report component.
- using factory patern (part of my design pattern learning).
- work only in java web environment

syntax to use the code
=======================

```java
ReportType reportType = ReportFactory.getReportExtension("pdf");
reportType.generateMasterSubReport(response, dataMap, listCards);
```
```java
ReportType reportType = ReportFactory.getReportExtension("pdf");
reportType.generateSingleReport(response, dataMap, listCards);
```

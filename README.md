# ParkWise Exercise 2

ParkWise Exercise 2 is a collaborative Java coursework project focused on an administrator-facing parking-management workflow. It is presented as an academic portfolio artifact, not as a production-ready application or a claim of sole authorship.

## What the source implements

- A Swing administrator interface for inspecting and operating parking conveyors.
- Parking-lot, vehicle, client, parking-session, conveyor, and price-list domain classes.
- Database-backed conveyor status, diagnostics, weight-management, and simulated work operations.
- Gson-based import of price-list JSON from `irs_prices_2026.json` into the local database.
- Annual-summary and club-growth report-controller paths.

The controller simulates conveyor diagnostics and work. The annual PDF/XML helpers in `ReportsController` are explicitly simulated. The administrator UI also contains simple XML and synthetic PDF output methods, but these were not executed or validated.

## Technology and dependencies

- JDK 21 - used to compile the source successfully during repository preparation.
- Java Swing - desktop administrator interface.
- Gson 2.10.1 - JSON parsing.
- Microsoft Access through UCanAccess 5.0.1 - required at runtime but not included in this repository.
- Retained UCanAccess support JARs: Jackcess 3.0.1, HSQLDB 2.5.0, Apache Commons Lang 3.8.1, and Apache Commons Logging 1.2.

No Maven or Gradle build is included.

## Project structure

```text
src/
|- App.java       application entry point
|- boundaries/    Swing UI and external-system interfaces
|- control/       parking, conveyor, pricing, and reporting logic
|- entity/        parking-domain objects
|- enums/         domain enumerations
|- utils/         database connection and JSON parsing helpers
`- lib/           retained dependency JARs
```

`App` is the application entry point. It performs database, price-list import, and report-controller calls before opening the administrator UI.

## Prerequisites

1. JDK 21. Use `java.exe` from the same JDK installation as `javac.exe`.
2. The UCanAccess 5.0.1 driver JAR, obtained separately from the [official UCanAccess distribution](https://ucanaccess.sourceforge.net/site.html). Its required runtime dependencies must also be available on the classpath.
3. A reviewed local `ParkWiseDB.accdb` in the repository root. The application uses this relative path at runtime; the database is deliberately not included in the repository.
4. `irs_prices_2026.json` in the repository root. `App` directly reads this file by name.

The repository retains several UCanAccess support JARs in `src/lib`, but it does not contain the UCanAccess driver itself. The complete runtime dependency setup remains a local prerequisite and has not been independently verified.

## Compile and launch (Windows PowerShell)

From the repository root:

```powershell
$jdk = 'C:\path\to\jdk-21\bin'
$ucanaccess = 'C:\path\to\ucanaccess-5.0.1.jar'
if (-not (Test-Path $ucanaccess)) { throw 'Set $ucanaccess to the UCanAccess 5.0.1 driver JAR.' }
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
New-Item -ItemType Directory -Force out | Out-Null
& "$jdk\javac.exe" -encoding UTF-8 -cp "src\lib\*;$ucanaccess" -d out $sources
& "$jdk\java.exe" -cp "out;src\lib\*;$ucanaccess" App
```

The source compilation step was verified with JDK 21 and the retained `src/lib` JARs. The complete launch command reflects the actual `App` entry point and its required UCanAccess driver, but it was not runtime-tested against a database. Running `App` may insert price-list data, generate report outputs, and open a Swing window.

Windows uses `;` as the classpath separator. On macOS or Linux, replace it with `:`; desktop and `Desktop` integration behavior has not been validated outside Windows.

## Validation status and limitations

- Confirmed: all Java source compiled successfully with JDK 21 using the retained `src/lib` JARs.
- Not tested: UCanAccess driver setup, database connectivity, JSON import execution, conveyor operations, Swing interaction, and report output.
- The report-controller PDF/XML helpers are simulations; do not represent them as verified document export.
- No automated tests or build automation were found.

## Local data and repository hygiene

The `.gitignore` excludes the local Access database and lock files, compiled output, the packaged application JAR, and generated report outputs. This includes the output names used by both the report controller and administrator UI.

Third-party JARs are retained for the coursework setup. Apache Commons Lang and Commons Logging include Apache 2.0 license information, and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) summarizes the verified HSQLDB 2.5.0 licensing evidence. Review the licensing and redistribution terms of all third-party JARs, including externally obtained UCanAccess artifacts, before making the repository public.

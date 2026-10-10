# ParkWise — Exercise 2

An early collaborative Java coursework exercise for a parking-management system. It is preserved as a coursework artifact for portfolio review; it is not presented as production software or as the work of one author.

## Confirmed scope

The source includes:

- a Swing-based administrator interface;
- parking-lot, vehicle, client, parking-session, conveyor, and price-list domain classes;
- conveyor and parking-lot controllers;
- JSON price-list import using Gson;
- an Access-database connection through UCanAccess; and
- a report-controller exercise. Its PDF/XML report-generation helpers are explicitly simulated in the current source, so they should not be treated as complete document export.

## Technology

- Java, compiled artifact inspected at Java 17; use JDK 21 for the documented commands.
- Swing and the Java standard library.
- Gson 2.10.1.
- UCanAccess 5.0.1 and its Access-driver dependencies.
- Microsoft Access database (`ParkWiseDB.accdb`) as a local runtime prerequisite.

No Maven or Gradle build is included.

## Source layout

```text
src/
├── App.java                 # application entry point
├── boundaries/              # Swing UI and external-system interfaces
├── control/                 # parking, conveyor, pricing, and report logic
├── entity/                  # domain objects
├── enums/                   # domain enumerations
├── utils/                   # database connection and JSON parsing
└── lib/                     # retained local dependency JARs
```

## Local setup

1. Install JDK 21. On this checkout, `javac` and `java` may resolve to different installations; use the `java.exe` in the same JDK `bin` directory as `javac.exe`.
2. Obtain the UCanAccess 5.0.1 driver from the [official UCanAccess distribution](https://ucanaccess.sourceforge.net/site.html), then set `C:\path\to\ucanaccess-5.0.1.jar` below to that driver JAR. UCanAccess also requires its runtime dependencies on the classpath. This project retains several related JARs in `src/lib` (including Jackcess, HSQLDB, and Apache Commons), but the complete local setup has not been independently runtime-verified; treat it as a prerequisite to verify locally.
3. Place a reviewed local copy of `ParkWiseDB.accdb` in the project root. It is deliberately ignored and is not supplied for publication.
4. Keep `irs_prices_2026.json` in the project root; `App` reads it by this relative name.

From PowerShell on Windows:

```powershell
$jdk = 'C:\path\to\jdk-21\bin'
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
& "$jdk\javac.exe" -cp 'src\lib\*;C:\path\to\ucanaccess-5.0.1.jar' -d out $sources
& "$jdk\java.exe" -cp 'out;src\lib\*;C:\path\to\ucanaccess-5.0.1.jar' App
```

The classpath separator is `;` on Windows and `:` on macOS/Linux. The Swing UI and `Desktop` integration are desktop-environment dependent; they have not been validated on non-Windows systems.

## Known limitations and validation status

- Runtime operation depends on the unreviewed local Access database and the separately supplied UCanAccess driver.
- The main program performs database, price-import, and report-controller exercise calls before opening the administrator UI; it may alter local database data when run.
- No automated test suite or build automation was found.
- The source compiled successfully with JDK 21 during repository preparation. No database-backed or GUI runtime check was performed because the local database is unreviewed.

## Repository hygiene

The ignore rules exclude local databases, Access lock files, build output, packaged JARs, and generated reports. The supplied library JARs and source JSON input are intentionally not ignored. Review course-sharing rules and obtain all collaborators' consent before publishing any version of this coursework.

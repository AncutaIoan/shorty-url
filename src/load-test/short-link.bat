@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

SET count=100
SET url=http://localhost:8080/api/short-links
SET data={"originalUrl":"https://example.com"}

REM === Start Test ===
echo Starting test with %count% requests to %url%...
echo ==============================

FOR /L %%i IN (1,1,%count%) DO (
    REM Start each request in a new process (parallel)
    start /B curl -s -o NUL -w "Request %%i Time: %%{time_total} seconds" -X POST %url% -H "Content-Type: application/json" -d "%data%"

    REM Add a new line after each request's output
    echo.
)

REM Wait a little while to allow all parallel requests to finish
timeout /t 5 > NUL

REM === End of Test ===
echo ==============================
echo Test completed. Total requests: %count%

ENDLOCAL
pause

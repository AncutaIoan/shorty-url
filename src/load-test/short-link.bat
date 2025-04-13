@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

:: Config
SET count=150
SET url=http://localhost:8080/api/short-links

:: Create payload
echo { "originalUrl": "https://example.com", "userId": "831ec447-1f5f-4638-9bde-673c2dcccd6d" } > payload.json

:: Fire off requests in parallel
FOR /L %%i IN (1,1,%count%) DO (
    echo Starting request %%i...
    start "" /B cmd /C curl -s -o NUL -w "Request %%i completed in %%{time_total} seconds" -X POST %url% -H "Content-Type: application/json" -d @payload.json
    echo .
)

:: Cleanup (wait before deleting payload if needed)
:: ping 127.0.0.1 -n 10 > nul
:: del payload.json

ENDLOCAL
pause

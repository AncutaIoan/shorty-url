@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

:: Config
SET count=150
SET url=http://localhost:8080/api/short-links

:: Create payload in a file
echo { "originalUrl": "https://example.com", "userId": "831ec447-1f5f-4638-9bde-673c2dcccd6d" } > payload.json

:: Loop requests
FOR /L %%i IN (1,1,%count%) DO (
    echo Sending request %%i...
    curl -s -o NUL -w "Request %%i completed in %%{time_total} seconds`n" -X POST %url% -H "Content-Type: application/json" -d @payload.json
    echo.
)

:: Clean up
del payload.json
ENDLOCAL
pause

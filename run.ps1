Get-Content .env | ForEach-Object {
    if ($_ -match '^([^#][^=]*)=(.*)') {
        [System.Environment]::SetEnvironmentVariable($Matches[1].Trim(), $Matches[2].Trim())
    }
}
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"

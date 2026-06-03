# Script de configuracion (requiere ejecutarse ELEVADO / como administrador).
# 1) Habilita el protocolo TCP/IP de la instancia por defecto de SQL Server.
# 2) Abre el puerto 1433 en el firewall de Windows.
# 3) Arranca el SQL Server Browser y reinicia el motor para aplicar cambios.
$ErrorActionPreference = 'Stop'
$logFile = 'E:\repos\tup\utn\PruebaVaadin\target\sql-setup.log'
New-Item -ItemType Directory -Force -Path (Split-Path $logFile) | Out-Null
function Log($m){ $line = "$m"; Add-Content -Path $logFile -Value $line; Write-Output $line }

try {
    $tcp = 'HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQLServer\SuperSocketNetLib\Tcp'

    # 1) Habilitar TCP/IP y fijar puerto 1433 en IPAll
    Set-ItemProperty -Path $tcp -Name Enabled -Value 1
    Set-ItemProperty -Path "$tcp\IPAll" -Name TcpPort -Value '1433'
    Set-ItemProperty -Path "$tcp\IPAll" -Name TcpDynamicPorts -Value ''
    Log "OK: TCP/IP habilitado (Enabled=1), puerto 1433 fijo."

    # 2) Regla de firewall para el puerto 1433 (idempotente)
    if (-not (Get-NetFirewallRule -DisplayName 'SQL Server 1433' -ErrorAction SilentlyContinue)) {
        New-NetFirewallRule -DisplayName 'SQL Server 1433' -Direction Inbound `
            -Protocol TCP -LocalPort 1433 -Action Allow | Out-Null
        Log "OK: regla de firewall 'SQL Server 1433' creada."
    } else {
        Log "OK: regla de firewall ya existia."
    }

    # 3) Reiniciar el motor (incluye dependencias) para aplicar el cambio de TCP/IP
    Restart-Service -Name MSSQLSERVER -Force
    Log "OK: servicio MSSQLSERVER reiniciado."

    Log "LISTO: configuracion aplicada correctamente."
    exit 0
}
catch {
    Log "ERROR: $($_.Exception.Message)"
    exit 1
}

#!/usr/bin/env node

const fs = require('fs');

// Leer el JSON que Claude Code pasa por stdin
let input = '';
process.stdin.on('data', chunk => input += chunk);
process.stdin.on('end', () => {
  try {
    const toolCall = JSON.parse(input);

    // Obtener la ruta del archivo que Claude intenta leer
    const filePath = toolCall.tool_input?.file_path || toolCall.tool_input?.path || '';
    const fileName = filePath.split('/').pop();

    // Archivos bloqueados por nombre exacto
    const blockedFiles = [
      'local.properties',
      'keystore.properties',
      'google-services.json'
    ];

    // Patrones de archivos/rutas bloqueados
    const blockedPatterns = [
      /BuildConfig\.(java|kt)$/,
      /\/build\/generated\/.*BuildConfig\.(java|kt)$/
    ];

    // Verificar nombre exacto
    if (blockedFiles.includes(fileName)) {
      console.error(`🔒 No puedo leer ${fileName}: contiene variables de entorno y secrets.`);
      process.exit(2);
    }

    // Verificar patrones
    const isBlocked = blockedPatterns.some(pattern => pattern.test(filePath));

    if (isBlocked) {
      console.error(`🔒 No puedo leer BuildConfig: son archivos generados que contienen secrets y API keys.`);
      process.exit(2);
    }

    process.exit(0); // PERMITIR
  } catch (e) {
    process.exit(2);
  }
});
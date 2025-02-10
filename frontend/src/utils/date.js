// src/utils/date.js
export function formatDate(timestamp, format = 'YYYY-MM-DD HH:mm:ss') {
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
  
    return format
      .replace('YYYY', year)
      .replace('MM', month)
      .replace('DD', day);
  }
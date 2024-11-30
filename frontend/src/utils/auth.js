// utils/auth.js
export function decodeJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    const decodedData = JSON.parse(jsonPayload);
    return decodedData;  // 确保返回的数据是对象，包括正确的 role 字段
}


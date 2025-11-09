interface EnvConfig {
  apiUrl: string;
  environment: 'development' | 'production';
  appName: string;
  appVersion: string;
  jwtSecretKey: string; // ⚠️ no mostrar secret real al frontend
  sessionTimeout: number;
}

const env: EnvConfig = {
  apiUrl: import.meta.env.VITE_API_URL || 'http://localhost:8084/api',
  environment: (import.meta.env.VITE_ENV as 'development' | 'production') || 'development',
  appName: import.meta.env.VITE_APP_NAME || 'MiApp',
  appVersion: import.meta.env.VITE_APP_VERSION || '1.0.0',
  jwtSecretKey: import.meta.env.VITE_JWT_SECRET_KEY || '',
  sessionTimeout: parseInt(import.meta.env.VITE_SESSION_TIMEOUT || '3600000', 10),
};

export default env;

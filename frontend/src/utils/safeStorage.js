// Safe local storage utility to prevent QuotaExceededError and unhandled runtime crashes
const memoryFallback = {};

export const safeStorage = {
  getItem: (key, defaultValue = null) => {
    try {
      if (typeof window === 'undefined' || !window.localStorage) {
        return memoryFallback[key] ?? defaultValue;
      }
      const val = window.localStorage.getItem(key);
      return val !== null ? val : defaultValue;
    } catch (err) {
      console.warn(`[safeStorage] Read failed for key "${key}":`, err);
      return memoryFallback[key] ?? defaultValue;
    }
  },

  getItemParsed: (key, defaultValue = null) => {
    try {
      const raw = safeStorage.getItem(key);
      if (!raw) return defaultValue;
      return JSON.parse(raw);
    } catch (err) {
      console.warn(`[safeStorage] JSON parse failed for key "${key}":`, err);
      return defaultValue;
    }
  },

  setItem: (key, value) => {
    const stringVal = typeof value === 'string' ? value : JSON.stringify(value);
    memoryFallback[key] = stringVal;

    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        window.localStorage.setItem(key, stringVal);
      }
    } catch (err) {
      console.warn(`[safeStorage] Write failed for key "${key}" (likely quota exceeded):`, err);
      // Attempt quota recovery by clearing old temporary caches
      try {
        if (typeof window !== 'undefined' && window.localStorage) {
          const keysToRemove = [];
          for (let i = 0; i < window.localStorage.length; i++) {
            const k = window.localStorage.key(i);
            if (k && (
              k.startsWith('fixconnect_chat_') ||
              k.startsWith('skillverse_notifications_') ||
              k.includes('temp') ||
              k.includes('cache')
            )) {
              keysToRemove.push(k);
            }
          }
          keysToRemove.forEach(k => window.localStorage.removeItem(k));
          // Try set one more time
          window.localStorage.setItem(key, stringVal);
        }
      } catch (innerErr) {
        // Silently fall back to memory storage
      }
    }
  },

  removeItem: (key) => {
    delete memoryFallback[key];
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        window.localStorage.removeItem(key);
      }
    } catch (err) {
      console.warn(`[safeStorage] Remove failed for key "${key}":`, err);
    }
  }
};

export default safeStorage;

// localStorage 工具类
const PREFIX = "TJSE_instrument_order";

/**
 * 保存数据到 localStorage
 * @param {String} key - 存储的键
 * @param {Object} value - 存储的值
 */
export function setItem(key, value) {
  try {
    const stringValue = JSON.stringify(value); // 将值转换为 JSON 字符串
    localStorage.setItem(PREFIX + key, stringValue);
  } catch (error) {
    console.error("localStorage setItem 错误:", error);
  }
}

/**
 * 从 localStorage 获取数据
 * @param {String} key - 获取的键
 * @returns {Object|null} - 返回解析后的值，找不到返回 null
 */
export function getItem(key) {
  try {
    const value = localStorage.getItem(PREFIX + key);
    return value ? JSON.parse(value) : null; // 解析 JSON 字符串
  } catch (error) {
    console.error("localStorage getItem 错误:", error);
    return null;
  }
}

/**
 * 从 localStorage 删除数据
 * @param {String} key - 删除的键
 */
export function removeItem(key) {
  try {
    localStorage.removeItem(PREFIX + key);
  } catch (error) {
    console.error("localStorage removeItem 错误:", error);
  }
}

/**
 * 清空所有 localStorage 数据
 */
export function clearStorage() {
  try {
    localStorage.clear();
  } catch (error) {
    console.error("localStorage clearStorage 错误:", error);
  }
}

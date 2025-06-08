from selenium import webdriver
from selenium.webdriver.edge.service import Service as EdgeService
from selenium.webdriver.common.by import By
import time
import os
from selenium.webdriver.edge.options import Options
import tempfile
import shutil
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import NoAlertPresentException

# -------------------- Create a unique user-data-dir ---------------------
temp_user_data_dir = tempfile.mkdtemp(prefix="selenium_profile_")  # 系统自动生成唯一文件夹
print(f"✅ 使用的用户目录: {temp_user_data_dir}")
# ----------------------------------------------------------------------

# ANSI color codes
COLOR_GREEN = "\033[92m"
COLOR_RED = "\033[91m"
COLOR_YELLOW = "\033[93m"
COLOR_BLUE = "\033[94m"
COLOR_RESET = "\033[0m"


# ----------------- CONFIG -------------------

EDGE_DRIVER_PATH = r"E:\EdgeDriver\msedgedriver.exe"
UPLOAD_IMG_VALID = r"D:\Projects\SE\TJ-se-design\final-project\uploads\images\1734789464220.jpg"
UPLOAD_IMG_INVALID = r"C:\Users\aleph\OneDrive\桌面\record.txt"
LOGIN_URL = "http://localhost:8081/"
TARGET_URL = "http://localhost:8081/profile"
WAIT_TIME = 2
long_desc = "A"*501
# ---------------------------------------------

# Test cases (R1–R10)
test_cases = [
    {
        "name": "Guitar",
        "price": "100.00",
        "rental_price": "50.00",
        "stock": "10",
        "rental_stock": "5",
        "description": "valid product",
        "file": UPLOAD_IMG_VALID,
    },
    {
        "name": "",
        "price": "100.00",
        "rental_price": "50.00",
        "stock": "10",
        "rental_stock": "5",
        "description": "null name",
        "file": UPLOAD_IMG_VALID,
    },
    {
        "name": "Valid",
        "price": "-5.00",
        "rental_price": "50.00",
        "stock": "10",
        "rental_stock": "5",
        "description": "price < 0",
        "file": UPLOAD_IMG_VALID,
    },
    {
        "name": "Valid",
        "price": "100.00",
        "rental_price": "abc",
        "stock": "10",
        "rental_stock": "5",
        "description": "rental_price is not a number",
        "file": UPLOAD_IMG_VALID,
    },
    {
        "name": "Valid",
        "price": "100.00",
        "rental_price": "50.00",
        "stock": "1000000",
        "rental_stock": "5",
        "description": "stock >= 1000000",
        "file": UPLOAD_IMG_VALID,
    },
    {
        "name": "Valid",
        "price": "100.00",
        "rental_price": "3.50",
        "stock": "10",
        "rental_stock": "-1",
        "description": "rental_stock < 0",
        "file": UPLOAD_IMG_VALID,
    },
    {
        "name": "Valid",
        "price": "100.00",
        "rental_price": "50.00",
        "stock": "10",
        "rental_stock": "5",
        "description": "invalid seller_id",  # 不测试登录状态，默认有效
        "file": UPLOAD_IMG_VALID,
    },
    {
        "name": "Valid",
        "price": "100.00",
        "rental_price": "50.00",
        "stock": "10",
        "rental_stock": "5",
        "description": "invalid is_active status", # 前端自动配置，不测试
        "file": UPLOAD_IMG_VALID,
    },
    {
        "name": "Valid",
        "price": "100.00",
        "rental_price": "50.00",
        "stock": "10",
        "rental_stock": "5",
        "description": "upload txt file",
        "file": UPLOAD_IMG_INVALID,
    },
    {
        "name": "Valid",
        "price": "100.00",
        "rental_price": "50.00",
        "stock": "10",
        "rental_stock": "5",
        "description": "no uploaded file",
        "file": None,
    },
    {
        "name": "Valid",
        "price": "100.00",
        "rental_price": "50.00",
        "stock": "10",
        "rental_stock": "5",
        "description": long_desc,
        "file": UPLOAD_IMG_VALID,
    },
    {
        "name": "",
        "price": "0.00",
        "rental_price": "50.00",
        "stock": "10",
        "rental_stock": "5",
        "description": "multiple invalid fields",
        "file": UPLOAD_IMG_INVALID,
    }
]


# ✅ Edge configuration
options = Options()
options.add_argument('--edge-skip-compat-layer-relaunch')
options.add_argument("--disable-gpu")
options.add_argument("--no-sandbox")
options.add_argument("--start-maximized")
options.add_argument(f"--user-data-dir={temp_user_data_dir}") 

# ✅ Launch Edge driver
service = EdgeService(executable_path=EDGE_DRIVER_PATH)
driver = webdriver.Edge(service=service, options=options)

# Step 1: Login
driver.get(LOGIN_URL)
time.sleep(WAIT_TIME)
driver.find_elements(By.TAG_NAME, "input")[0].send_keys("lzh")
driver.find_elements(By.TAG_NAME, "input")[1].send_keys("123456")
driver.find_element(By.XPATH, '//button[contains(text(), "登录")]').click()
time.sleep(WAIT_TIME)


# ✅ Step 2: Handle alert if present
try:
    alert = driver.switch_to.alert
    print(f"{COLOR_YELLOW}Alert detected: {alert.text}{COLOR_RESET}")
    alert.accept()
    time.sleep(WAIT_TIME)
except:
    print(f"{COLOR_GREEN}No alert detected, proceeding{COLOR_RESET}")

# Step 3: Navigate to "Add Product" page
try:
    driver.get(TARGET_URL)
    time.sleep(WAIT_TIME)
    wait = WebDriverWait(driver, 2)
    upload_button = wait.until(EC.element_to_be_clickable((By.XPATH, '//button[contains(text(), "上架商品")]')))
    upload_button.click()
    time.sleep(WAIT_TIME)
    print(f"{COLOR_GREEN}Successfully navigated to the product upload page{COLOR_RESET}")
except Exception as e:
    print(f"{COLOR_RED}Failed to click 'Add Product' button: {e}{COLOR_RESET}")
    driver.quit()
    shutil.rmtree(temp_user_data_dir, ignore_errors=True)
    exit()

# Step 4: Execute each test case
success_upload_count = 0
failed_upload_count = 0

for idx, case in enumerate(test_cases):
    print(f"{COLOR_BLUE}[R{idx+1}] Executing test case: {case['description']}{COLOR_RESET}")
    
    # Skip specific cases not testable from frontend
    if "invalid seller_id" in case["description"] or "invalid is_active status" in case["description"]:
        print(f"{COLOR_YELLOW}Test R{idx+1} skipped due to backend-only field: {case['description']}{COLOR_RESET}")
        print(f"{COLOR_RED}Test R{idx+1}: Counted as product upload failed (not testable via frontend){COLOR_RESET}")
        failed_upload_count += 1
        continue

    try:
        driver.get(TARGET_URL)
        time.sleep(WAIT_TIME)

        wait = WebDriverWait(driver, 2)
        upload_button = wait.until(EC.element_to_be_clickable((By.XPATH, '//button[contains(text(), "上架商品")]')))
        upload_button.click()
        time.sleep(WAIT_TIME)

        # Fill form
        if case["file"]:
            driver.find_element(By.XPATH, '//input[@type="file"]').send_keys(case["file"])
        driver.find_elements(By.TAG_NAME, 'input')[1].clear()
        driver.find_elements(By.TAG_NAME, 'input')[1].send_keys(case["name"])
        driver.find_elements(By.TAG_NAME, 'input')[2].clear()
        driver.find_elements(By.TAG_NAME, 'input')[2].send_keys(case["price"])
        driver.find_elements(By.TAG_NAME, 'input')[3].clear()
        driver.find_elements(By.TAG_NAME, 'input')[3].send_keys(case["rental_price"])
        driver.find_elements(By.TAG_NAME, 'input')[4].clear()
        driver.find_elements(By.TAG_NAME, 'input')[4].send_keys(case["stock"])
        driver.find_elements(By.TAG_NAME, 'input')[5].clear()
        driver.find_elements(By.TAG_NAME, 'input')[5].send_keys(case["rental_stock"])
        driver.find_element(By.TAG_NAME, 'textarea').clear()
        driver.find_element(By.TAG_NAME, 'textarea').send_keys(case["description"])

        # Submit form
        driver.find_element(By.XPATH, '//form//button[@type="submit" and contains(text(), "上架商品")]').click()
        time.sleep(WAIT_TIME)

        # Check alert
        try:
            alert = driver.switch_to.alert
            alert_text = alert.text
            print(f"{COLOR_YELLOW}Alert message: {alert_text}{COLOR_RESET}")
            alert.accept()

            if "商品上传成功" in alert_text:
                print(f"{COLOR_GREEN}Test R{idx+1}: Product uploaded successfully{COLOR_RESET}")
                success_upload_count += 1
            else:
                print(f"{COLOR_RED}Test R{idx+1}: Product NOT uploaded successfully{COLOR_RESET}")
                failed_upload_count += 1

        except NoAlertPresentException:
            print(f"{COLOR_BLUE}No alert shown{COLOR_RESET}")
            print(f"{COLOR_RED}Test R{idx+1}: Product NOT uploaded successfully (no confirmation){COLOR_RESET}")
            failed_upload_count += 1

    except Exception as e:
        print(f"{COLOR_RED}Test R{idx+1} failed with error: {e}{COLOR_RESET}")
        failed_upload_count += 1

# ✅ Cleanup
driver.quit()
shutil.rmtree(temp_user_data_dir, ignore_errors=True)
print(f"{COLOR_GREEN}Temporary Edge user-data-dir cleaned up{COLOR_RESET}")

# ✅ Summary
print(f"\n{COLOR_BLUE}==== TEST EXECUTION SUMMARY ===={COLOR_RESET}")
print(f"{COLOR_GREEN}Uploaded successfully: {success_upload_count}{COLOR_RESET}")
print(f"{COLOR_RED}Upload failed: {failed_upload_count}{COLOR_RESET}")
print(f"{COLOR_BLUE}Total test cases executed: {success_upload_count + failed_upload_count}{COLOR_RESET}")

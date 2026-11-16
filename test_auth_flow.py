import urllib.request, urllib.parse, http.cookiejar, re

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# 1. Fetch login page to get CSRF token
resp = opener.open('http://localhost:8080/login')
html = resp.read().decode('utf-8')
csrf_token = re.search(r'name="_csrf"\s+value="([^"]+)"', html).group(1)
print('CSRF Token extracted:', csrf_token[:15] + '...')

# 2. Test invalid login
data_bad = urllib.parse.urlencode({'username': 'manager', 'password': 'wrongpassword', '_csrf': csrf_token}).encode('utf-8')
resp_bad = opener.open('http://localhost:8080/login', data=data_bad)
print('Invalid login resulted in URL:', resp_bad.geturl())
assert 'error=true' in resp_bad.geturl(), "Expected redirect to /login?error=true"

# 3. Test valid login
resp_login_page = opener.open('http://localhost:8080/login')
html2 = resp_login_page.read().decode('utf-8')
csrf_token2 = re.search(r'name="_csrf"\s+value="([^"]+)"', html2).group(1)
data_good = urllib.parse.urlencode({'username': 'manager', 'password': 'manager123', '_csrf': csrf_token2}).encode('utf-8')
resp_good = opener.open('http://localhost:8080/login', data=data_good)
print('Valid login resulted in URL:', resp_good.geturl())
assert '/dashboard' in resp_good.geturl(), "Expected redirect to /dashboard"
cookies = [c.name + '=' + c.value for c in cj]
print('Cookies after login:', cookies)
print('ALL AUTHENTICATION TESTS PASSED!')

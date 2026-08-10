# ساخت APK سرمایه‌یار با GitHub Actions

1. در GitHub یک Repository جدید بسازید، مثلاً `SarmayeYar`.
2. تمام فایل‌های این پوشه را داخل Repository قرار دهید.
3. Commit و Push کنید.
4. وارد زبانه `Actions` شوید.
5. Workflow با نام `Build SarmayeYar APK` را انتخاب کنید.
6. در صورت نیاز `Run workflow` را بزنید.
7. پس از پایان موفق، در بخش `Artifacts` فایل `SarmayeYar-debug-apk` را دانلود کنید.
8. فایل ZIP دانلودشده را باز کنید؛ داخل آن `app-debug.apk` قرار دارد.

نکته:
- برای این روش Android Studio و Android SDK روی کامپیوتر شما لازم نیست.
- Build روی runner لینوکسی GitHub انجام می‌شود.
- این نسخه debug است و برای تست روی گوشی مناسب است.
- برای انتشار نهایی در Google Play بعداً باید نسخه release و signing اضافه شود.

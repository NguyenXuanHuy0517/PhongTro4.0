FROM ubuntu:20.04 AS build
ENV DEBIAN_FRONTEND=noninteractive

# Cài đặt các công cụ cần thiết cho Flutter
RUN apt-get update && apt-get install -y \
    curl git unzip xz-utils zip libglu1-mesa

# Clone Flutter SDK (stable branch to support Dart 3.10+)
RUN git clone https://github.com/flutter/flutter.git -b stable /usr/local/flutter
ENV PATH="/usr/local/flutter/bin:/usr/local/flutter/bin/cache/dart-sdk/bin:${PATH}"

# Cấu hình an toàn cho thư mục flutter
RUN git config --global --add safe.directory /usr/local/flutter

WORKDIR /app
COPY . .

# Build ứng dụng Flutter Web
RUN flutter clean
RUN flutter pub get
RUN flutter build web --release

# Sử dụng Nginx để serve web
FROM nginx:alpine
COPY --from=build /app/build/web /usr/share/nginx/html

# Ghi đè file cấu hình nginx mặc định để hỗ trợ client-side routing
RUN echo 'server { \
    listen       80; \
    server_name  localhost; \
    location / { \
        root   /usr/share/nginx/html; \
        index  index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
    error_page   500 502 503 504  /50x.html; \
    location = /50x.html { \
        root   /usr/share/nginx/html; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

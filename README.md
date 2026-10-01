# 🛡️ N VPN — Fast. Safe. Unlimited.

**Repository:** `mariaislam1812/Nvpn`  
**Platform:** Android (Jetpack Compose, Kotlin, Android `VpnService`, Room, Coroutines) & Cross-Platform Blueprint (Android / iOS)

---

## ✨ Key Features

1. **Brand Identity & Minimalist Cyber Dark UI**
   - Custom **N VPN** Shield + Lock + Speed vector monogram logo
   - Tagline: **"Fast. Safe. Unlimited."**
   - Deep Obsidian (`#070B14`), Cyber Emerald (`#00F5D4`), and Electric Cyan (`#38BDF8`) dark theme
2. **One-Tap VPN Connection (`NVpnService`)**
   - Android native `VpnService` TUN interface integration
   - Real-time session timer (`HH:MM:SS`), Masked Virtual IP vs. Real ISP IP detection
3. **Global Server Selection & Smart Connect**
   - Multi-country Free & 25Gbps VIP server nodes (Singapore, Bangladesh BDIX, Germany, USA, Japan, UK, Switzerland, Netherlands, India, Australia)
   - **Smart Connect** automatically benchmarks socket latency (`ms`) and server load to connect to the fastest node
   - **Custom VPS Node Importer** (`+ VPS`): Connect your own Ubuntu WireGuard / OpenVPN VPS directly inside the app
4. **Protocols & Security**
   - **WireGuard®** (`ChaCha20-Poly1305` · UDP 51820)
   - **OpenVPN** (`AES-256-GCM` · UDP 1194 & Stealth TCP 443)
   - **IKEv2 / IPsec** (Mobile seamless handover)
   - **Internet Kill Switch** (`Builder.setBlocking(true)`) to block unprotected traffic if the tunnel drops
   - **Strict Zero-Logs Policy (RAM-Disk Mode)** & Encrypted DNS (`1.1.1.1`, `9.9.9.9`, `8.8.8.8`)
5. **Real-Time Speed Meter**
   - Live Download & Upload throughput monitor (`TrafficStats`) with a real-time dual-curve graph
6. **Monetization (VIP Subscription & AdMob)**
   - Free Tier with Google AdMob Banner & Rewarded Video Ad (`+2 Hours VIP Pass`)
   - VIP Pro Subscription plans (Monthly & Annual)

---

## 🚀 How to Build & Run

1. Clone this repository:
   ```bash
   git clone https://github.com/mariaislam1812/Nvpn.git
   cd Nvpn
   ```
2. Open in **Android Studio** (Ladybug or newer) and run on an Android device or emulator, or build via Gradle:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🖥️ Quick Ubuntu 24.04 WireGuard VPS Setup

```bash
apt update && apt install -y wireguard iptables qrencode
sysctl -w net.ipv4.ip_forward=1
umask 077
wg genkey | tee /etc/wireguard/server_private.key | wg pubkey > /etc/wireguard/server_public.key
```

---

## 📄 License & Contact

**Copyright by nazmul 2026**  
**Contact:** [nazmulhasansojib51@Gmail.com](mailto:nazmulhasansojib51@Gmail.com)

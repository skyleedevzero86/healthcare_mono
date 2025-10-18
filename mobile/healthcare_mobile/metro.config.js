const { getDefaultConfig } = require('expo/metro-config');

const config = getDefaultConfig(__dirname);

config.server = {
  ...config.server,
  port: 9081,
};

// Force port 9081
process.env.EXPO_DEVTOOLS_LISTEN_ADDRESS = '0.0.0.0:9081';

module.exports = config;

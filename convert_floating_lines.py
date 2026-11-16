import os
import re

jsx_path = r'C:\Users\divya\.gemini\antigravity\scratch\stratops360\FloatingLines.jsx'
with open(jsx_path, 'r', encoding='utf-8') as f:
    src = f.read()

# 1. Clean preamble: remove React imports
preamble = src[:src.find('const FloatingLines =')]
preamble = preamble.replace("'use client';", '')
preamble = preamble.replace("import { useEffect, useRef } from 'react';", '')
preamble = preamble.replace("import './FloatingLines.css';", '')

# 2. Extract component content
comp_str = src[src.find('const FloatingLines ='):]
eff_token = '  useEffect(() => {\n'
eff_idx = comp_str.find(eff_token)
settings_block = comp_str[comp_str.find('  const waves ='):eff_idx]

# Clean settingsRef in settings_block
settings_block = settings_block.replace('const settingsRef = useRef(settings);\n  settingsRef.current = settings;', 'const settingsRef = { current: settings };')
settings_block = settings_block.replace('const settingsRef = useRef(settings);\r\n  settingsRef.current = settings;', 'const settingsRef = { current: settings };')

# Inside the effect
eff_content = comp_str[eff_idx + len(eff_token):]
cleanup_str = '    return () => {\n'
cleanup_idx = eff_content.find(cleanup_str)

main_eff = eff_content[:cleanup_idx]
cleanup_body_start = cleanup_idx + len(cleanup_str)
end_eff_marker = '    };\n  }, []);'
end_eff_idx = eff_content.find(end_eff_marker)
cleanup_body = eff_content[cleanup_body_start:end_eff_idx]

# Replace wakeRef.current with wakeFn
main_eff_clean = main_eff.replace('wakeRef.current = () => {', 'wakeFn = () => {')
main_eff_clean = main_eff_clean.replace('const container = containerRef.current;\n    const canvas = canvasRef.current;', '// container and canvas already resolved')
main_eff_clean = main_eff_clean.replace('const container = containerRef.current;\r\n    const canvas = canvasRef.current;', '// container and canvas already resolved')
main_eff_clean = main_eff_clean.replace('containerRef.current', 'container')
main_eff_clean = main_eff_clean.replace('canvasRef.current', 'canvas')

cleanup_body_clean = cleanup_body.replace('wakeRef.current = null;', 'wakeFn = null;')

vanilla_code = f"""/**
 * FloatingLines - React Bits Background Component
 * Variant: JS-CSS (Vanilla JavaScript + CSS)
 * Source: https://reactbits.dev/backgrounds/floating-lines
 * Registry: https://reactbits.dev/r/FloatingLines-JS-CSS.json
 */

(function (root, factory) {{
  if (typeof define === 'function' && define.amd) {{
    define([], factory);
  }} else if (typeof module === 'object' && module.exports) {{
    module.exports = factory();
  }} else {{
    root.FloatingLines = factory();
    root.initFloatingLines = root.FloatingLines.init;
  }}
}})(typeof self !== 'undefined' ? self : this, function () {{
'use strict';

{preamble.strip()}

  function initFloatingLines(containerOrSelector, userOptions) {{
    var options = userOptions || {{}};
    var container = typeof containerOrSelector === 'string'
      ? document.querySelector(containerOrSelector)
      : containerOrSelector;

    if (!container) {{
      console.warn('initFloatingLines: Container element not found', containerOrSelector);
      return null;
    }}

    var canvas = container.querySelector('canvas.floating-lines-canvas');
    if (!canvas) {{
      canvas = document.createElement('canvas');
      canvas.className = 'floating-lines-canvas';
      canvas.setAttribute('aria-hidden', 'true');
      container.appendChild(canvas);
    }}

    var linesGradient = options.linesGradient !== undefined ? options.linesGradient : ['#e945f5', '#2f4bc0', '#e945f5'];
    var enabledWaves = options.enabledWaves !== undefined ? options.enabledWaves : ['top', 'middle', 'bottom'];
    var lineCount = options.lineCount !== undefined ? options.lineCount : 10;
    var lineDistance = options.lineDistance !== undefined ? options.lineDistance : 6;
    var topWavePosition = options.topWavePosition;
    var middleWavePosition = options.middleWavePosition;
    var bottomWavePosition = options.bottomWavePosition;
    var animationSpeed = options.animationSpeed !== undefined ? options.animationSpeed : 1;
    var amplitude = options.amplitude !== undefined ? options.amplitude : 1;
    var lineWidth = options.lineWidth !== undefined ? options.lineWidth : 1;
    var lineStyle = options.lineStyle !== undefined ? options.lineStyle : 'solid';
    var glow = options.glow !== undefined ? options.glow : 1;
    var brightness = options.brightness !== undefined ? options.brightness : 1;
    var depthOfField = options.depthOfField !== undefined ? options.depthOfField : 0.5;
    var edgeFade = options.edgeFade !== undefined ? options.edgeFade : 0;
    var interactive = options.interactive !== undefined ? options.interactive : true;
    var bendRadius = options.bendRadius !== undefined ? options.bendRadius : 5;
    var bendStrength = options.bendStrength !== undefined ? options.bendStrength : -0.5;
    var mouseDamping = options.mouseDamping !== undefined ? options.mouseDamping : 0.05;
    var parallax = options.parallax !== undefined ? options.parallax : true;
    var parallaxStrength = options.parallaxStrength !== undefined ? options.parallaxStrength : 0.2;
    var clickSwirl = options.clickSwirl !== undefined ? options.clickSwirl : true;
    var intro = options.intro !== undefined ? options.intro : true;
    var mixBlendMode = options.mixBlendMode !== undefined ? options.mixBlendMode : 'screen';
    var backgroundColor = options.backgroundColor;
    var lightMode = options.lightMode !== undefined ? options.lightMode : false;
    var opacity = options.opacity !== undefined ? options.opacity : 1;
    var dpr = options.dpr;

{settings_block}
    var wakeFn = null;

{main_eff_clean}

    function destroy() {{
{cleanup_body_clean}
    }}

    return {{
      destroy: destroy,
      wake: function() {{ if (wakeFn) wakeFn(); }},
      updateSettings: function(newOpts) {{
        if (!newOpts) return;
        Object.assign(options, newOpts);
        if (wakeFn) wakeFn();
      }}
    }};
  }}

  return {{
    init: initFloatingLines
  }};
}});
"""

out_js = r'C:\Users\divya\.gemini\antigravity\scratch\stratops360\src\main\resources\static\js\floating-lines.js'
with open(out_js, 'w', encoding='utf-8') as f:
    f.write(vanilla_code)

print('floating-lines.js written, length:', len(vanilla_code))

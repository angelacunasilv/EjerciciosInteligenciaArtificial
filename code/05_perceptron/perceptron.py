import numpy as np
from sklearn.linear_model import SGDRegressor
from sklearn.preprocessing import StandardScaler

# 1. Datos de entrenamiento (Celsius y sus equivalentes exactos en Fahrenheit)
X_celsius = np.array([-40, -20, 0, 10, 20, 30, 40, 100]).reshape(-1, 1)
y_fahrenheit = X_celsius.ravel() * 1.8 + 32

# 2. Escalado de datos (fundamental para que el descenso de gradiente converja rápido y bien)
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X_celsius)

# 3. Entrenar la neurona lineal (Regresión con Descenso de Gradiente Estocástico)
regressor = SGDRegressor(max_iter=1000, tol=1e-3, learning_rate='invscaling', eta0=0.01, random_state=42)
regressor.fit(X_scaled, y_fahrenheit)

# ==========================================
# 4. Obtener W y Bias del modelo (Escalados)
# ==========================================
w_scaled = regressor.coef_[0]
bias_scaled = regressor.intercept_[0]

print("--- PARÁMETROS EN EL ESPACIO ESCALADO ---")
print(f"Peso (W): {w_scaled:.4f}")
print(f"Sesgo (Bias): {bias_scaled:.4f}")

# ==========================================
# 5. Reconstruir W y Bias a la escala original (°C)
# ==========================================
# Fórmula de transformación inversa del StandardScaler:
# X_scaled = (X - media) / desviacion_estandar
# Por tanto: W_original = W_scaled / desviacion_estandar
# Bias_original = Bias_scaled - (W_scaled * media / desviacion_estandar)

media = scaler.mean_[0]
desviacion = scaler.scale_[0]

w_original = w_scaled / desviacion
bias_original = bias_scaled - (w_scaled * media / desviacion)

print("\n--- PARÁMETROS EN LA ESCALA ORIGINAL (°C) ---")
print(f"Peso original (W): {w_original:.4f}  (Idealmente cercano a 1.8)")
print(f"Sesgo original (Bias): {bias_original:.4f} (Idealmente cercano a 32.0)")

# Comprobación de la ecuación de la recta aprendida: F = (W_original * C) + Bias_original
print(f"\nEcuación aprendida: F = {w_original:.2f} * C + {bias_original:.2f}")
 
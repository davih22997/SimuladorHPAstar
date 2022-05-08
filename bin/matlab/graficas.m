% Datos
% a. Punto inicial -> textData
% b. Punto final -> textData
% 1. Longitud de la solución óptima -> data
% 2. Longitud de HPA* -> data
% 3. Tiempo de A* -> data
% 4. Tiempo de preprocesamiento HPA* -> data
% 5. Tiempo de introducción inicio/fin en HPA* -> data
% 6. Tiempo de refinamiento HPA* -> data
% 7. Nodos A* (iteraciones) -> data
% 8. Nodos HPA* (iteraciones) -> data
% 9. Porcentaje de error -> data

% Cogemos el archivo con los datos
clear all;
close all;
clc;
filename = 'datos.txt';
delimiterIn = ' ';
headerlinesIn = 1;
A = importdata(filename, delimiterIn, headerlinesIn);

% Gráfica de tiempo de CPU:
% Cogemos los datos

x = A.data(:, 1); % Longitud de la solución
tA = A.data(:, 3); % Tiempo de CPU de A*
tpreHPA = A.data(:, 4); % Tiempo de preprocesamiento en HPA*
tESHPA = A.data(:, 5); % Tiempo de introducción de inicio/fin en HPA*
trefHPA = A.data(:, 6); % Tiempo de refinamiento en HPA*
tHPA = tESHPA + trefHPA; % Tiempo de CPU de HPA*

% Ordenamos los valores conforme a los de la longitud de la solución
[xsort,I] = sort(x);
tAsort = tA(I);
tpreHPAsort = tpreHPA(I);
tESHPAsort = tESHPA(I);
trefHPAsort = trefHPA(I);
tHPAsort = tHPA(I);


% Mostramos la gráfica
figure
p1 = plot(xsort, tAsort, xsort, tHPAsort);

p1(1).Marker = "*";
p1(2).Marker = "*";

title('Tiempo de CPU');
xlabel('Longitud de la solución');
ylabel('Total de tiempo de CPU (segundos)');
legend('A*', 'HPA*');

% Gráfica de número de nodos
% Cogemos los datos (la x la tenemos ya)
iA = A.data(:, 7); % Número de nodos de A*
iHPA = A.data(:, 8); % Número de nodos de HPA*

% Ordenamos los valores conforme a los de x (longitud de la solución)
iAsort = iA(I);
iHPAsort = iHPA(I);

% Mostramos la gráfica
figure
p2 = plot(xsort, iAsort, xsort, iHPAsort);

p2(1).Marker = "*";
p2(2).Marker = "*";

title('Total de nodos expandidos');
xlabel('Longitud de la solución');
ylabel('Número de nodos');
legend('A*', 'HPA*');

% Gráfica de la calidad de la solución
% Cogemos los datos (la x la tenemos ya)
e = A.data(:, 9); % Porcentaje de error

% Ordenamos los valores conforme a los de x (longitud de la solución)
esort = e(I);

% Mostramos la gráfica
figure
p3 = plot(xsort, esort);

p3(1).Marker = "*";

title('Calidad de la solución');
xlabel('Longitud de la solución');
ylabel('Porcentaje de error');
legend('HPA*');

% Creamos las gráficas de barras comparativas
figure
% Creamos copias sin valores nan
xs = xsort(~isnan(xsort));
tpre = tpreHPAsort(~isnan(tpreHPAsort));
tes = tESHPAsort(~isnan(tESHPAsort));
tref = trefHPAsort(~isnan(trefHPAsort));

% Cantidad de valores
n = length(xs);
% Dividimos entre 5 para agrupar 5 veces
n2 = n / 5;
y(:,1) = tes;
y(:,2) = tref;

[counts, centers] = hist(xs, 5);



y = zeros(5,2);

val = (centers(1) + centers(2))/ 2;
val = val - centers(1);

% Agrupamos
for i = 1:5
    % Cogemos los índices de los valores que corresponden al primer
    % intervalo
    v1 = centers(i) - val;
    v2 = centers(i) + val;
    k = find(xs >= v1 & xs < v2);
    % Agrupamos los valores
    y(i,1) = mean(tes(k));
    y(i,2) = mean(tref(k));

end

% Mostramos la gráfica con los resultados
p4 = bar(centers, y, 1.0, 'stacked');
legend('Introducción de puntos inicio/fin', 'Búsqueda en grafo abstracto + refinamiento');
title('Comparativa de tiempo por fase');
xlabel('Longitud de la solución');
ylabel('Tiempo de CPU (segundos)');







# Χρησιμοποιούμε Node για το χτίσιμο
FROM node:20-alpine
WORKDIR /app

# Αντιγραφή αρχείων ρυθμίσεων
COPY package*.json ./
RUN npm install

# Αντιγραφή όλου του κώδικα
COPY . .

# Άνοιγμα της θύρας που χρησιμοποιεί το Vite (συνήθως 5173 ή 3000)
EXPOSE 5173

# Εκκίνηση της εφαρμογής
CMD ["npm", "run", "dev", "--", "--host"]
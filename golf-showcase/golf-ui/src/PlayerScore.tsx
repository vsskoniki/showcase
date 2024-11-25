import React, { useState } from 'react';

const PlayerScore: React.FC = () => {
  // State to store form data
  const [formData, setFormData] = useState({
    name: '',
    hole: '',
    score: '',
  });

  // State to handle form submission status
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submissionStatus, setSubmissionStatus] = useState<string | null>(null);

  // Handler for form input changes
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  // Handler for form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Simple validation
    if (!formData.name || !formData.hole || !formData.score) {
      setSubmissionStatus('Please fill out all fields.');
      return;
    }

    setIsSubmitting(true);
    setSubmissionStatus(null);

    try {
      // Assuming the REST API endpoint is 'https://your-api-url.com/contact'
      const response = await fetch('http://localhost:8080/api/score', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (response.ok) {
        setSubmissionStatus('Form submitted successfully!');
      } else {
        setSubmissionStatus('Failed to submit form. Try again later.');
      }
    } catch (error) {
      setSubmissionStatus('Error: Could not submit the form.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div>
      
      <form onSubmit={handleSubmit}>
      <table>
          <tbody>
            <tr>
              <th><label htmlFor="name">Name:</label></th>
              <td>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                />
              </td>
            </tr>

            <tr>
              <th><label htmlFor="hole">Hole:</label></th>
              <td>
                <input
                  type="hole"
                  id="hole"
                  name="hole"
                  value={formData.hole}
                  onChange={handleChange}
                  required
                />
              </td>
            </tr>
            <tr>
              <th><label htmlFor="score">Score:</label></th>
              <td>
                <input
                  type="score"
                  id="score"
                  name="score"
                  value={formData.score}
                  onChange={handleChange}
                  required
                />
              </td>
            </tr>
            
          </tbody>
        </table>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Submitting...' : 'Submit'}
        </button>
      </form>

      {submissionStatus && <p>{submissionStatus}</p>}
    </div>
  );
};

export default PlayerScore;

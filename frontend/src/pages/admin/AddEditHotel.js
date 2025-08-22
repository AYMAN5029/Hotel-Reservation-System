import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner, Image } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import { hotelAPI } from '../../services/api';
import { toast } from 'react-toastify';
import { FaHotel, FaSave, FaTimes, FaUpload, FaImage } from 'react-icons/fa';

const AddEditHotel = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = !!id;

  const [loading, setLoading] = useState(isEditing);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [uploadingImage, setUploadingImage] = useState(false);
  const [hotelData, setHotelData] = useState({
    hotelName: '',
    address: '',
    city: '',
    state: '',
    country: '',
    description: '',
    avgRatingByCustomers: 0,
    acRoomCost: '',
    nonAcRoomCost: '',
    totalAcRooms: 0,
    totalNonAcRooms: 0,
    availableAcRooms: 0,
    availableNonAcRooms: 0
  });

  useEffect(() => {
    if (isEditing) {
      loadHotelData();
    }
  }, [id, isEditing]);

  const loadHotelData = async () => {
    try {
      const response = await hotelAPI.getHotelById(id);
      const hotelInfo = response.data;
      setHotelData(hotelInfo);
      // Set image preview if hotel has an image
      if (hotelInfo.imagePath) {
        setImagePreview(hotelAPI.getHotelImageUrl(id));
      }
    } catch (error) {
      toast.error('Failed to load hotel data');
      console.error('Error loading hotel:', error);
      navigate('/admin/hotels');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    const numericValue = type === 'number' ? parseFloat(value) || 0 : value;
    
    let updatedData = {
      ...hotelData,
      [name]: numericValue
    };

    // Auto-set available rooms when total rooms change for new hotels
    if (!isEditing) {
      if (name === 'totalAcRooms') {
        updatedData.availableAcRooms = numericValue;
      } else if (name === 'totalNonAcRooms') {
        updatedData.availableNonAcRooms = numericValue;
      }
    }

    // Ensure available rooms don't exceed total rooms
    if (name === 'availableAcRooms' && numericValue > hotelData.totalAcRooms) {
      updatedData.availableAcRooms = hotelData.totalAcRooms;
    } else if (name === 'availableNonAcRooms' && numericValue > hotelData.totalNonAcRooms) {
      updatedData.availableNonAcRooms = hotelData.totalNonAcRooms;
    }

    setHotelData(updatedData);
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Validate file type
      if (!file.type.startsWith('image/')) {
        toast.error('Please select a valid image file');
        return;
      }
      
      // Validate file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        toast.error('Image size should be less than 5MB');
        return;
      }

      setSelectedImage(file);
      
      // Create preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleImageUpload = async () => {
    if (!selectedImage || !id) return;

    setUploadingImage(true);
    try {
      await hotelAPI.uploadHotelImage(id, selectedImage);
      toast.success('Hotel image uploaded successfully!');
      setSelectedImage(null);
      setImagePreview(null);
      
      // Refresh hotel data to get updated imagePath
      const updatedHotel = await hotelAPI.getHotelById(id);
      setHotelData(updatedHotel.data);
      
      // Set the new image preview from server
      if (updatedHotel.data.imagePath) {
        setImagePreview(hotelAPI.getHotelImageUrl(id));
      }
    } catch (error) {
      toast.error('Failed to upload image');
      console.error('Error uploading image:', error);
    } finally {
      setUploadingImage(false);
    }
  };

  const validateForm = () => {
    const requiredFields = ['hotelName', 'address', 'city', 'state', 'country', 'acRoomCost', 'nonAcRoomCost', 'totalAcRooms', 'totalNonAcRooms'];
    const missingFields = requiredFields.filter(field => !hotelData[field]);
    
    if (missingFields.length > 0) {
      setError(`Please fill in all required fields: ${missingFields.join(', ')}`);
      return false;
    }

    if (hotelData.acRoomCost <= 0 || hotelData.nonAcRoomCost <= 0) {
      setError('Room costs must be greater than 0');
      return false;
    }

    if (hotelData.avgRatingByCustomers < 0 || hotelData.avgRatingByCustomers > 5) {
      setError('Rating must be between 0 and 5');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!validateForm()) {
      return;
    }

    setSaving(true);

    try {
      let hotelId;
      if (isEditing) {
        await hotelAPI.updateHotel(id, hotelData);
        hotelId = id;
        toast.success('Hotel updated successfully!');
      } else {
        const response = await hotelAPI.addHotel(hotelData);
        hotelId = response.data.hotelId;
        toast.success('Hotel added successfully!');
      }
      
      // Upload image if one was selected
      if (selectedImage && hotelId) {
        try {
          await hotelAPI.uploadHotelImage(hotelId, selectedImage);
          toast.success('Hotel image uploaded successfully!');
        } catch (imageError) {
          console.error('Error uploading image:', imageError);
          toast.warning('Hotel saved but image upload failed');
        }
      }
      
      navigate('/admin/hotels');
    } catch (error) {
      const errorMessage = error.response?.data?.message || 
                          error.response?.data || 
                          `Failed to ${isEditing ? 'update' : 'add'} hotel`;
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Row>
          <Col className="text-center">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <div className="admin-panel">
      <Container className="py-4">
        <Row>
          <Col>
            <div className="d-flex justify-content-between align-items-center mb-4">
              <h2>
                <FaHotel className="me-2" />
                {isEditing ? 'Edit Hotel' : 'Add New Hotel'}
              </h2>
              <Button 
                variant="outline-secondary" 
                onClick={() => navigate('/admin/hotels')}
              >
                <FaTimes className="me-2" />
                Cancel
              </Button>
            </div>
          </Col>
        </Row>

        <Row>
          <Col lg={8}>
            <Card>
              <Card.Body>
                {error && <Alert variant="danger">{error}</Alert>}

                <Form onSubmit={handleSubmit}>
                  <Row>
                    <Col md={6}>
                      <Form.Group className="mb-3">
                        <Form.Label>Hotel Name *</Form.Label>
                        <Form.Control
                          type="text"
                          name="hotelName"
                          value={hotelData.hotelName}
                          onChange={handleChange}
                          placeholder="Enter hotel name"
                          required
                        />
                      </Form.Group>
                    </Col>
                    <Col md={6}>
                      <Form.Group className="mb-3">
                        <Form.Label>Average Rating</Form.Label>
                        <Form.Control
                          type="number"
                          name="avgRatingByCustomers"
                          value={hotelData.avgRatingByCustomers}
                          onChange={handleChange}
                          placeholder="0.0"
                          min="0"
                          max="5"
                          step="0.1"
                        />
                        <Form.Text className="text-muted">
                          Rating between 0 and 5 stars
                        </Form.Text>
                      </Form.Group>
                    </Col>
                  </Row>

                  <Form.Group className="mb-3">
                    <Form.Label>Address *</Form.Label>
                    <Form.Control
                      type="text"
                      name="address"
                      value={hotelData.address}
                      onChange={handleChange}
                      placeholder="Enter full address"
                      required
                    />
                  </Form.Group>

                  <Row>
                    <Col md={4}>
                      <Form.Group className="mb-3">
                        <Form.Label>City *</Form.Label>
                        <Form.Control
                          type="text"
                          name="city"
                          value={hotelData.city}
                          onChange={handleChange}
                          placeholder="Enter city"
                          required
                        />
                      </Form.Group>
                    </Col>
                    <Col md={4}>
                      <Form.Group className="mb-3">
                        <Form.Label>State *</Form.Label>
                        <Form.Control
                          type="text"
                          name="state"
                          value={hotelData.state}
                          onChange={handleChange}
                          placeholder="Enter state"
                          required
                        />
                      </Form.Group>
                    </Col>
                    <Col md={4}>
                      <Form.Group className="mb-3">
                        <Form.Label>Country *</Form.Label>
                        <Form.Control
                          type="text"
                          name="country"
                          value={hotelData.country}
                          onChange={handleChange}
                          placeholder="Enter country"
                          required
                        />
                      </Form.Group>
                    </Col>
                  </Row>

                  <Form.Group className="mb-3">
                    <Form.Label>Description</Form.Label>
                    <Form.Control
                      as="textarea"
                      rows={4}
                      name="description"
                      value={hotelData.description}
                      onChange={handleChange}
                      placeholder="Enter hotel description, amenities, and features..."
                    />
                  </Form.Group>

                  <Form.Group className="mb-3">
                    <Form.Label>Hotel Image</Form.Label>
                    <div className="d-flex flex-column gap-3">
                      {imagePreview && (
                        <div>
                          <Image 
                            src={imagePreview} 
                            alt="Hotel preview" 
                            thumbnail 
                            style={{ maxWidth: '300px', maxHeight: '200px' }}
                          />
                        </div>
                      )}
                      <div className="d-flex gap-2 align-items-center">
                        <Form.Control
                          type="file"
                          accept="image/*"
                          onChange={handleImageChange}
                          className="flex-grow-1"
                        />
                        {selectedImage && isEditing && (
                          <Button
                            variant="outline-primary"
                            onClick={handleImageUpload}
                            disabled={uploadingImage}
                            size="sm"
                          >
                            {uploadingImage ? (
                              <>
                                <Spinner animation="border" size="sm" className="me-2" />
                                Uploading...
                              </>
                            ) : (
                              <>
                                <FaUpload className="me-2" />
                                Upload Now
                              </>
                            )}
                          </Button>
                        )}
                      </div>
                      <Form.Text className="text-muted">
                        {!isEditing 
                          ? "Select an image file (max 5MB) - it will be uploaded when you save the hotel" 
                          : selectedImage 
                            ? "Click 'Upload Now' to update the image immediately, or save the form to upload with other changes"
                            : "Select an image file (max 5MB) to upload for this hotel"
                        }
                      </Form.Text>
                    </div>
                  </Form.Group>

                  <Row>
                    <Col md={6}>
                      <Form.Group className="mb-3">
                        <Form.Label>AC Room Cost (per night) *</Form.Label>
                        <Form.Control
                          type="number"
                          name="acRoomCost"
                          value={hotelData.acRoomCost}
                          onChange={handleChange}
                          placeholder="0.00"
                          min="0"
                          step="0.01"
                          required
                        />
                      </Form.Group>
                    </Col>
                    <Col md={6}>
                      <Form.Group className="mb-3">
                        <Form.Label>Non-AC Room Cost (per night) *</Form.Label>
                        <Form.Control
                          type="number"
                          name="nonAcRoomCost"
                          value={hotelData.nonAcRoomCost}
                          onChange={handleChange}
                          placeholder="0.00"
                          min="0"
                          step="0.01"
                          required
                        />
                      </Form.Group>
                    </Col>
                  </Row>

                  <hr className="my-4" />
                  <h6 className="mb-3">Room Inventory Management</h6>
                  
                  <Row>
                    <Col md={6}>
                      <Form.Group className="mb-3">
                        <Form.Label>Total AC Rooms *</Form.Label>
                        <Form.Control
                          type="number"
                          name="totalAcRooms"
                          value={hotelData.totalAcRooms}
                          onChange={handleChange}
                          placeholder="0"
                          min="0"
                          required
                        />
                        <Form.Text className="text-muted">
                          Total number of AC rooms in the hotel
                        </Form.Text>
                      </Form.Group>
                    </Col>
                    <Col md={6}>
                      <Form.Group className="mb-3">
                        <Form.Label>Total Non-AC Rooms *</Form.Label>
                        <Form.Control
                          type="number"
                          name="totalNonAcRooms"
                          value={hotelData.totalNonAcRooms}
                          onChange={handleChange}
                          placeholder="0"
                          min="0"
                          required
                        />
                        <Form.Text className="text-muted">
                          Total number of Non-AC rooms in the hotel
                        </Form.Text>
                      </Form.Group>
                    </Col>
                  </Row>

                  {isEditing && (
                    <Row>
                      <Col md={6}>
                        <Form.Group className="mb-3">
                          <Form.Label>Available AC Rooms</Form.Label>
                          <Form.Control
                            type="number"
                            name="availableAcRooms"
                            value={hotelData.availableAcRooms}
                            onChange={handleChange}
                            placeholder="0"
                            min="0"
                            max={hotelData.totalAcRooms}
                          />
                          <Form.Text className="text-muted">
                            Currently available AC rooms (max: {hotelData.totalAcRooms})
                          </Form.Text>
                        </Form.Group>
                      </Col>
                      <Col md={6}>
                        <Form.Group className="mb-3">
                          <Form.Label>Available Non-AC Rooms</Form.Label>
                          <Form.Control
                            type="number"
                            name="availableNonAcRooms"
                            value={hotelData.availableNonAcRooms}
                            onChange={handleChange}
                            placeholder="0"
                            min="0"
                            max={hotelData.totalNonAcRooms}
                          />
                          <Form.Text className="text-muted">
                            Currently available Non-AC rooms (max: {hotelData.totalNonAcRooms})
                          </Form.Text>
                        </Form.Group>
                      </Col>
                    </Row>
                  )}

                  <div className="d-flex gap-2 mt-4">
                    <Button
                      variant="secondary"
                      onClick={() => navigate('/admin/hotels')}
                    >
                      Cancel
                    </Button>
                    <Button
                      variant="primary"
                      type="submit"
                      disabled={saving}
                      className="flex-grow-1"
                    >
                      {saving ? (
                        <>
                          <Spinner animation="border" size="sm" className="me-2" />
                          {isEditing ? 'Updating...' : 'Adding...'}
                        </>
                      ) : (
                        <>
                          <FaSave className="me-2" />
                          {isEditing ? 'Update Hotel' : 'Add Hotel'}
                        </>
                      )}
                    </Button>
                  </div>
                </Form>
              </Card.Body>
            </Card>
          </Col>

          <Col lg={4}>
            <Card className="sticky-top" style={{ top: '20px' }}>
              <Card.Body>
                <h5 className="mb-3">Hotel Information</h5>
                <div className="mb-2">
                  <strong>Required Fields:</strong>
                  <ul className="small mt-2">
                    <li>Hotel Name</li>
                    <li>Address</li>
                    <li>City, State, Country</li>
                    <li>AC Room Cost</li>
                    <li>Non-AC Room Cost</li>
                  </ul>
                </div>
                <div className="mb-2">
                  <strong>Optional Fields:</strong>
                  <ul className="small mt-2">
                    <li>Description</li>
                    <li>Average Rating (0-5)</li>
                  </ul>
                </div>
                <div className="bg-light p-2 rounded mt-3">
                  <small className="text-muted">
                    <strong>Tip:</strong> Provide detailed descriptions and accurate pricing to help customers make informed decisions.
                  </small>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default AddEditHotel;
